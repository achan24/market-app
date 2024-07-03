import React, { useState, useEffect, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { useAuth } from './AuthContext'


import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

import { formatDistanceToNow } from 'date-fns';


const ListingPage = () => {
  const { id } = useParams()
  const [listing, setListing] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [comments, setComments] = useState([])
  const [newComment, setNewComment] = useState('')
  const [offerAmount, setOfferAmount] = useState('')
  const { user, token, isAuthenticated } = useAuth()
  
  const buttonRef = useRef(null)
  const textareaRef = useRef(null)

  useEffect(() => {
    const fetchListing = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch listing');
        }
        const data = await response.json();
        setListing(data);
        console.log(data)
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    const fetchComments = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}/comments`);
        if (!response.ok) {
          throw new Error('Failed to fetch comments');
        }
        const data = await response.json();
        setComments(data);
      } catch (err) {
        console.error('Error fetching comments:', err);
      }
    }

    fetchListing()
    fetchComments()
  }, [id]);


  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      alert("Please log in to post a comment.");
      return;
    }
    console.log('Sending comment:', { comment: newComment });
    console.log('Token being sent:', token);
    try {
      const response = await fetch(`http://localhost:8000/api/v1/listings/${id}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ comment: newComment }),
      });
  
      let responseData;
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        responseData = await response.json();
      } else {
        responseData = await response.text();
      }
      console.log('Server response:', responseData);

      // const responseData = await response.json();
      // console.log('Server response:', responseData);
  
      if (response.ok) {
        //update the comments
        window.location.reload();
        // const newCommentObj = {
        //   id: 0,
        //   comment: newComment,
        //   user: { username: user.username },
        //   createdAt: new Date().toISOString()
        // };
  
        // // Update the UI
        // setListing(prevListing => ({
        //   ...prevListing,
        //   comments: [...(prevListing.comments || []), newCommentObj],
        // }));
        // setNewComment('');
      } else {
        throw new Error('Failed to post comment');
      }
    } catch (error) {
      console.error('Error posting comment:', error);
      if (error.response) {
        console.error(error.response.data);
        console.error(error.response.status);
        console.error(error.response.headers);
      } else if (error.request) {
        console.error(error.request);
      } else {
        console.error('Error', error.message);
      }
      alert('Failed to post comment. Please try again.');
    }
  };

  const handleOfferSubmit = (e) => {
    e.preventDefault();
    // Check if the current user is the seller
    if (user.username === listing.sellerName || user.id === listing.sellerId) {
      alert("You cannot make an offer on your own listing.");
      return;
    }
    
    if (!isAuthenticated) {
      alert("Please log in to place an offer.");
      return;
    }
    
    setNewComment(`OFFER: EUR${offerAmount}`)
    
    setTimeout(() => {
      buttonRef.current.click()
    }, 200);
  }


  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!listing) return <div>No listing found</div>;

  const firstImage = listing.images && listing.images.length > 0 ? `data:${listing.images[0].fileType};base64,${listing.images[0].data}` : null;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <img src={firstImage} alt={listing.title} className="w-full h-auto rounded-lg shadow-lg border border-gray-200" />
          <div className="mt-4 grid grid-cols-4 gap-2">
            {listing.images.slice(1).map((image, index) => (
              <img key={index} src={image.url} alt={`${listing.title} - ${index + 2}`} className="w-full h-auto rounded-lg" />
            ))}
          </div>
        </div>
        <div>
          <h1 className="text-3xl font-bold mb-4">{listing.title}</h1>
          <p className="text-xl font-semibold mb-2">€{listing.askingPrice}</p>
          <div className="flex items-center mb-4">
            <Avatar className="h-10 w-10">
              <AvatarImage src={listing.sellerAvatar} alt={listing.sellerName} />
              <AvatarFallback>{listing.sellerName.charAt(0).toUpperCase()}</AvatarFallback>
            </Avatar>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-900">{listing.sellerName}</p>
              <p className="text-xs text-gray-500">Seller</p>
            </div>
          </div>
          <p className="mb-2"><strong>Description:</strong> 
            <span className="text-gray-600 mb-4">{listing.description}</span>
          </p>

          <p className="mb-2"><strong>Category:</strong> {listing.category}</p>
          <p className="text-gray-500 mb-4"><strong>Location:</strong> {listing.location}</p>

          
          <div className="mb-6">
            <h2 className="text-xl font-semibold mb-2">Shipping Options</h2>
            <ul className="list-disc pl-5">
              <li>Standard Shipping: €5.99</li>
              <li>Express Shipping: €12.99</li>
              <li>Local Pickup: Free</li>
            </ul>
          </div>
          
          <div className="mb-6">
            <h2 className="text-xl font-semibold mb-2">Payment Options</h2>
            <ul className="list-disc pl-5">
              <li>Credit/Debit Card</li>
              <li>PayPal</li>
              <li>Bank Transfer</li>
            </ul>
          </div>
          
          <form onSubmit={handleOfferSubmit} className="mb-6">
            <Input
              type="number"
              value={offerAmount}
              onChange={(e) => setOfferAmount(e.target.value)}
              placeholder="Enter your offer amount"
              className="mb-2"
            />
            <Button type="submit" className="w-full">Place Offer</Button>
          </form>
        </div>
      </div>
      

      <div className="mt-12">
        <h2 className="text-2xl font-bold mb-4">Comments</h2>
        {comments.map((comment, index) => (
          <div key={index} className="mb-4 p-4 bg-white border border-gray-200 rounded-lg">
            <div className="flex items-start mb-2">
              <div className="mr-4">
                <Avatar>
                  <AvatarFallback>{comment.username[0].toUpperCase()}</AvatarFallback>
                </Avatar>
              </div>
              <div className="flex-1">
                <div className="flex items-center justify-between">
                  <p className="font-semibold">{comment.username}</p>
                  <span className="text-sm text-gray-500 ml-2">
                    {formatDistanceToNow(new Date(comment.createdAt), { addSuffix: true })}
                  </span>
                </div>
                <p className="mt-1">{comment.comment?.startsWith("OFFER:") ?
                  comment.comment.replace("EUR", '€') :
                  comment.comment
                }</p>
              </div>
            </div>
          </div>
        ))}
      
      <form onSubmit={handleCommentSubmit} className="mt-6">
        <Textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Write a comment..."
          className="mb-2"
          name="comment-textarea"
          ref={textareaRef}
        />
        <Button type="submit" ref={buttonRef}>Post Comment</Button>
      </form>
    </div>
      




    </div>
  );
};

export default ListingPage;