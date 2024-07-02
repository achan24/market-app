import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'


// Import your UI components
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

const ListingPage = () => {
  const { id } = useParams();
  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newComment, setNewComment] = useState('');
  const [offerAmount, setOfferAmount] = useState('');

  useEffect(() => {
    const fetchListing = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch listing');
        }
        const data = await response.json();
        setListing(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchListing();
  }, [id]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    // Implement comment submission logic here
    console.log('Submitting comment:', newComment);
    setNewComment('');
  };

  const handleOfferSubmit = async (e) => {
    e.preventDefault();
    // Implement offer submission logic here
    console.log('Submitting offer:', offerAmount);
    setOfferAmount('');
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!listing) return <div>No listing found</div>;

  const firstImage = listing.images && listing.images.length > 0 ? `data:${listing.images[0].fileType};base64,${listing.images[0].data}` : null;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <img src={firstImage} alt={listing.title} className="w-full h-auto rounded-lg shadow-lg" />
          <div className="mt-4 grid grid-cols-4 gap-2">
            {listing.images.slice(1).map((image, index) => (
              <img key={index} src={image.url} alt={`${listing.title} - ${index + 2}`} className="w-full h-auto rounded-lg" />
            ))}
          </div>
        </div>
        <div>
          <h1 className="text-3xl font-bold mb-4">{listing.title}</h1>
          <p className="text-xl font-semibold mb-2">€{listing.askingPrice}</p>
          <p className="text-gray-600 mb-4">{listing.description}</p>
          <p className="mb-2"><strong>Category:</strong> {listing.category}</p>
          <p className="mb-4"><strong>Location:</strong> {listing.location}</p>
          
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
        {listing.comments && listing.comments.map((comment, index) => (
          <div key={index} className="mb-4 p-4 bg-gray-100 rounded-lg">
            <div className="flex items-center mb-2">
              <Avatar src={comment.user.avatar} alt={comment.user.name} />
              <div className="ml-2">
                <p className="font-semibold">{comment.user.name}</p>
                <p className="text-sm text-gray-500">{new Date(comment.date).toLocaleDateString()}</p>
              </div>
            </div>
            <p>{comment.text}</p>
          </div>
        ))}
        
        <form onSubmit={handleCommentSubmit} className="mt-6">
          <Textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Write a comment..."
            className="mb-2"
          />
          <Button type="submit">Post Comment</Button>
        </form>
      </div>
    </div>
  );
};

export default ListingPage;