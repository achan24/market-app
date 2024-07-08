import React, { useState, useEffect } from 'react';
import { Send } from 'lucide-react';
import { useAuth } from './AuthContext';

const Inbox = () => {
  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageInput, setMessageInput] = useState('');
  const [messages, setMessages] = useState([]);
  const [listingDetails, setListingDetails] = useState({});
  const { user, token } = useAuth();

  useEffect(() => {
    if (token) {
      fetchUserConversations();
    }
  }, [token]);

  const fetchUserConversations = async () => {
    try {
      const response = await fetch('http://localhost:8000/api/v1/conversations', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        throw new Error('Failed to fetch conversations');
      }
      const conversationsData = await response.json();
      setConversations(conversationsData);
      fetchAllListingDetails(conversationsData);
    } catch (error) {
      console.error('Error fetching conversations:', error);
    }
  };

  const fetchAllListingDetails = async (conversations) => {
    try {
      const listingDetailsPromises = conversations.map(async (conversation) => {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${conversation.listingId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch listing details');
        }
        const data = await response.json();
        return { listingId: conversation.listingId, details: data };
      });

      const listingDetailsArray = await Promise.all(listingDetailsPromises);
      const listingDetailsObject = listingDetailsArray.reduce((acc, listing) => {
        acc[listing.listingId] = listing.details;
        return acc;
      }, {});

      setListingDetails(listingDetailsObject);
    } catch (error) {
      console.error('Error fetching listing details:', error);
    }
  };

  const handleSelectConversation = async (conversation) => {
    setSelectedConversation(conversation);
    try {
      const response = await fetch(`http://localhost:8000/api/v1/conversations/${conversation.id}/messages`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        throw new Error('Failed to fetch messages');
      }
      const data = await response.json();
      console.log('Fetched messages:', data); // Log received messages
      setMessages(data);
    } catch (error) {
      console.error('Error fetching messages:', error);
    }
  };

  const sendMessage = async () => {
    if (!selectedConversation || !messageInput.trim()) return;
    try {
      const response = await fetch(`http://localhost:8000/api/v1/conversations/${selectedConversation.id}/messages`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ content: messageInput }),
      });
      if (!response.ok) {
        throw new Error('Failed to send message');
      }
      
      const data = await response.json();
      console.log('Sent message:', data); // Log sent message
      setMessages(prevMessages => [...prevMessages, { ...data, sender: user }]);
      setMessageInput('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  const getImageSrc = (image) => {
    return `data:${image.fileType};base64,${image.data}`;
  };

  const displayMessageContent = (content) => {
    try {
      const parsedContent = JSON.parse(content);
      return parsedContent.content || content;
    } catch {
      return content;
    }
  };

  return (
    <div className="container mx-auto p-4 font-sans">
      <h1 className="text-2xl font-bold mb-4">Inbox</h1>
      <div className="flex bg-gray-100 rounded-lg shadow">
        {/* Conversation List */}
        <div className="w-1/3 border-r border-gray-300 p-4 overflow-y-auto max-h-[80vh]">
          {conversations.length > 0 ? (
            conversations.map((conversation) => (
              <div
                key={conversation.id}
                className={`p-4 cursor-pointer hover:bg-gray-200 ${
                  selectedConversation?.id === conversation.id ? 'bg-gray-200' : ''
                } flex items-center space-x-4 mb-4`}
                onClick={() => handleSelectConversation(conversation)}
              >
                <div className="flex-shrink-0 w-20 h-20">
                  {listingDetails[conversation.listingId] && listingDetails[conversation.listingId].images ? (
                    <img 
                      src={getImageSrc(listingDetails[conversation.listingId].images[0])}
                      alt={listingDetails[conversation.listingId]?.title || 'Listing'} 
                      className="w-full h-full object-cover rounded"
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = '/placeholder-image.jpg';
                      }}
                    />
                  ) : (
                    <div className="w-full h-full bg-gray-300 rounded"></div>
                  )}
                </div>
                <div className="flex-grow">
                  <h3 className="font-semibold">{listingDetails[conversation.listingId]?.title || 'Loading...'}</h3>
                  <p className="text-sm text-gray-600">
                    Price: ${listingDetails[conversation.listingId]?.acceptedPrice || listingDetails[conversation.listingId]?.askingPrice || 'N/A'}
                  </p>
                </div>
              </div>
            ))
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-600">No active conversations.</p>
            </div>
          )}
        </div>

        {/* Conversation Detail */}
        <div className="w-2/3 p-4">
          {selectedConversation ? (
            <>
              <h2 className="text-xl font-semibold mb-4">
                {listingDetails[selectedConversation.listingId]?.title || 'Loading...'}
              </h2>
              <p className="text-sm text-gray-600 mb-4">
                Buyer: {listingDetails[selectedConversation.listingId]?.buyerName || 'Unknown'}
              </p>
              <p className="text-sm text-gray-600 mb-4">
                Seller: {listingDetails[selectedConversation.listingId]?.sellerName || 'Unknown'}
              </p>
              {/* Messages */}
              <div className="space-y-3 mb-4 max-h-96 overflow-y-auto">
                {messages.length > 0 ? (
                  messages.map((message, index) => (
                    <div
                      key={index}
                      className={`flex ${
                        message.sender.username === user.username ? 'justify-end' : 'justify-start'
                      }`}
                    >
                      <div className={`p-2 rounded-lg ${
                        message.sender.username === user.username 
                          ? 'bg-blue-500 text-white' 
                          : 'bg-gray-300'
                      }`}>
                        {displayMessageContent(message.content)}
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-8">
                    <p className="text-gray-600">No messages in this conversation.</p>
                  </div>
                )}
              </div>

              {/* Message Input */}
              <div className="flex items-center mt-4">
                <input
                  type="text"
                  value={messageInput}
                  onChange={(e) => setMessageInput(e.target.value)}
                  placeholder="Type something..."
                  className="flex-grow p-2 border border-gray-300 rounded-l-full focus:outline-none focus:ring-2 focus:ring-blue-300"
                />
                <button
                  className="bg-orange-500 text-white p-2 rounded-r-full hover:bg-orange-600 transition"
                  onClick={sendMessage}
                >
                  <Send size={20} />
                </button>
              </div>
            </>
          ) : (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <h2 className="text-xl font-semibold text-gray-700 mb-2">Welcome to your Inbox!</h2>
                <p className="text-gray-600">Select a conversation from the left to view messages.</p>
                <p className="text-sm text-gray-500 mt-4">Your active buying and selling conversations will appear here.</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Inbox;
