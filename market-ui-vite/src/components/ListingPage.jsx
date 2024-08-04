import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { formatDistanceToNow } from 'date-fns';
import { PayPalScriptProvider, PayPalButtons } from '@paypal/react-paypal-js';

const ListingPage = () => {
  const { id } = useParams();
  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [offerAmount, setOfferAmount] = useState('');
  const { user, token, isAuthenticated } = useAuth();
  const [acceptedOffer, setAcceptedOffer] = useState(null);
  const [paypalClientId, setPaypalClientId] = useState(null);
  const [sellerEmail, setSellerEmail] = useState(null);
  const [sellerProfilePic, setSellerProfilePic] = useState(null);
  const [commenterProfilePics, setCommenterProfilePics] = useState({});

  const navigate = useNavigate();

  useEffect(() => {
    const fetchListing = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}`);
        if (!response.ok) {
          throw new Error('Failed to fetch listing');
        }
        const data = await response.json();
        setListing(data);

        // Fetch seller email and profile picture after listing is fetched
        fetchSellerEmail(data.sellerName);
        fetchUserProfilePic(data.sellerName, setSellerProfilePic);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    const fetchComments = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}/comments`);
        if (!response.ok) {
          throw new Error('Failed to fetch comments');
        }
        const data = await response.json();
        setComments(data);

        // Fetch profile pictures for each commenter
        const profilePicPromises = data.map(comment => fetchUserProfilePic(comment.username));
        const profilePics = await Promise.all(profilePicPromises);
        const profilePicMap = {};
        data.forEach((comment, index) => {
          profilePicMap[comment.username] = profilePics[index];
        });
        setCommenterProfilePics(profilePicMap);
      } catch (err) {
        console.error('Error fetching comments:', err);
      }
    };

    const fetchPaypalClientId = async () => {
      try {
        const response = await fetch('http://localhost:8000/api/paypal-client-id', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        const data = await response.json();
        setPaypalClientId(data.clientId);
      } catch (err) {
        console.error('Error fetching PayPal client ID:', err);
      }
    };

    const fetchSellerEmail = async (sellerName) => {
      try {
        const response = await fetch(`http://localhost:8000/user/email/${sellerName}`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        if (response.ok) {
          const data = await response.json();
          setSellerEmail(data.email);
        } else {
          console.error('Failed to fetch seller email');
        }
      } catch (err) {
        console.error('Error fetching seller email:', err);
      }
    };

    const fetchUserProfilePic = async (username, setProfilePic) => {
      try {
        const response = await fetch(`http://localhost:8000/user/profile-pic/${username}`);
        if (response.ok) {
          const blob = await response.blob();
          const url = URL.createObjectURL(blob);
          setProfilePic(url);
        } else {
          console.error('Failed to fetch user profile picture, status:', response.status);
        }
      } catch (error) {
        console.error('Error fetching user profile picture:', error);
      }
    };

    fetchListing();
    fetchComments();
    fetchPaypalClientId();
  }, [id, token]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      alert("Please log in to post a comment.");
      return;
    }
    try {
      const response = await fetch(`http://localhost:8000/api/v1/listings/${id}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ comment: newComment }),
      });

      if (response.ok) {
        window.location.reload();
      } else {
        throw new Error('Failed to post comment');
      }
    } catch (error) {
      console.error('Error posting comment:', error);
      alert('Failed to post comment. Please try again.');
    }
  };

  const handleOfferSubmit = (e) => {
    e.preventDefault();
    if (user.username === listing.sellerName) {
      alert("You cannot make an offer on your own listing.");
      return;
    }

    if (!isAuthenticated) {
      alert("Please log in to place an offer.");
      return;
    }

    setNewComment(`OFFER: EUR${offerAmount}`);
    setTimeout(() => {
      document.getElementById("submit-comment").click();
    }, 200);
  };

  const handleAcceptOffer = async (comment) => {
    try {
      const buyerUsername = comment.username;
      const acceptedPriceString = comment.comment.slice(10).trim();
      const acceptedPrice = parseFloat(acceptedPriceString);

      const response = await fetch(`http://localhost:8000/api/v1/listings/${listing.id}/acceptOffer`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          buyerUsername,
          acceptedPrice
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to accept offer');
      }

      const updatedListing = await response.json();
      setListing(updatedListing);
      setAcceptedOffer(comment);
    } catch (error) {
      console.error('Error accepting offer:', error);
      alert('Failed to accept offer. Please try again.');
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!listing) return <div>No listing found</div>;

  const firstImage = listing.images && listing.images.length > 0 ? `data:${listing.images[0].fileType};base64,${listing.images[0].data}` : null;

  return (
    <PayPalScriptProvider options={{ 
      "client-id": paypalClientId,
      "currency": "EUR"}}
    >
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <img src={firstImage} alt={listing.title} className="w-full h-auto rounded-lg shadow-lg border border-gray-200" />
            <div className="mt-4 grid grid-cols-4 gap-2">
              {listing.images.slice(1).map((image, index) => (
                <img key={index} src={`data:${image.fileType};base64,${image.data}`} alt={`${listing.title} - ${index + 2}`} className="w-full h-auto rounded-lg" />
              ))}
            </div>
          </div>
          <div>
            <h1 className="text-3xl font-bold mb-4">{listing.title}</h1>
            <p className="text-xl font-semibold mb-2">€{listing.askingPrice}</p>
            <div className="flex items-center mb-4">
              <Avatar className="h-10 w-10">
                <AvatarImage src={sellerProfilePic || 'https://github.com/shadcn.png'} alt={listing.sellerName} />
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
                    <AvatarImage src={commenterProfilePics[comment.username] || 'https://github.com/shadcn.png'} />
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

                  {comment.comment?.startsWith("OFFER:") && (
                    <>
                      {listing.status === 'SOLD' ? (
                        <p className="text-green-500 text-lg">Sold</p>
                      ) : (
                        <>
                          {listing.buyerName && listing.buyerName === comment.username && listing.acceptedPrice != null ? (
                            <>
                              <p className="text-green-500 text-lg">Offer Accepted</p>
                              {user.username === comment.username && paypalClientId && sellerEmail && (
                                <div className="w-full max-w-xs">
                                  <PayPalButtons
                                    style={{
                                      layout: 'vertical',
                                      color: 'gold',
                                      shape: 'rect',
                                      label: 'paypal',
                                      height: 40,
                                    }}
                                    createOrder={(data, actions) => {
                                      return actions.order.create({
                                        purchase_units: [{
                                          amount: {
                                            value: listing.acceptedPrice.toString(),
                                            currency_code: 'EUR'
                                          },
                                          payee: {
                                            email_address: sellerEmail
                                          },
                                          description: listing.title
                                        }]
                                      });
                                    }}
                                    onApprove={(data, actions) => {
                                      return actions.order.capture().then((details) => {
                                        //alert('Transaction completed by ' + details.payer.name.given_name);
                                        navigate(`/payment-success/${listing.id}`, { state: { title: listing.title } });
                                      });
                                    }}
                                  />
                                </div>
                              )}
                            </>
                          ) : (
                            isAuthenticated && user.username === listing.sellerName && (
                              <Button onClick={() => handleAcceptOffer(comment)}>Accept Offer</Button>
                            )
                          )}
                        </>
                      )}
                    </>
                  )}
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
            />
            <Button type="submit" id="submit-comment">Post Comment</Button>
          </form>
        </div>
      </div>
    </PayPalScriptProvider>
  );
};

export default ListingPage;
