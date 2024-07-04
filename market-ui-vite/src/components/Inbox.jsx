import React, { useState, useEffect } from 'react';
import { Send } from 'lucide-react';

const Inbox = () => {
  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageInput, setMessageInput] = useState('');

  useEffect(() => {
    const fetchUserConversations = async () => {
      try {
        const response = await fetch('/api/v1/conversations');
        console.log(response)
        const data = await response.json();
        console.log(data)
        setConversations(data);
      } catch (error) {
        console.error('Error fetching conversations:', error);
      }
    };

    fetchUserConversations();
  }, []);

  const handleSelectConversation = (conversation) => {
    setSelectedConversation(conversation);
  };

  const sendMessage = async () => {
    try {
      const response = await fetch(`/api/v1/conversations/${selectedConversation.id}/messages`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(messageInput),
      });
      const data = await response.json();
      setSelectedConversation((prevConversation) => ({
        ...prevConversation,
        messages: [...prevConversation.messages, data],
      }));
      setMessageInput('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="container mx-auto p-4 font-sans">
      <h1 className="text-2xl font-bold mb-4">Inbox</h1>
      <div className="flex bg-gray-100 rounded-lg shadow">
        {/* Conversation List */}
        <div className="w-1/3 border-r border-gray-300 p-4">
          {conversations.map((conversation) => (
            <div
              key={conversation.id}
              className={`p-4 cursor-pointer hover:bg-gray-200 ${
                selectedConversation?.id === conversation.id ? 'bg-gray-200' : ''
              }`}
              onClick={() => handleSelectConversation(conversation)}
            >
              {/* Conversation list items */}
            </div>
          ))}
        </div>

        {/* Conversation Detail */}
        <div className="w-2/3 p-4">
          {selectedConversation && (
            <>
              {/* Messages */}
              <div className="space-y-3 mb-4 max-h-96 overflow-y-auto">
                {selectedConversation.messages.map((message, index) => (
                  <div
                    key={index}
                    className={`flex ${
                      message.sender.username === 'you' ? 'justify-end' : 'justify-start'
                    }`}
                  >
                    {/* Message items */}
                  </div>
                ))}
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
          )}
        </div>
      </div>
    </div>
  );
};

export default Inbox;