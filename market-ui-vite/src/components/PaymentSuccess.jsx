import React, { useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

const PaymentSuccess = () => {
  const { id } = useParams();
  const location = useLocation();
  const { title } = location.state || {};
  const { user, token, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const updateListingStatus = async () => {
      try {
        const response = await fetch(`http://localhost:8000/api/v1/listings/${id}/status?status=SOLD`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
        });

        if (!response.ok) {
          throw new Error('Failed to update listing status');
        }

        console.log('Listing status updated to SOLD');
      } catch (error) {
        console.error('Error updating listing status:', error);
      }
    };

    updateListingStatus();

    const timer = setTimeout(() => {
      navigate(`/listing/${id}`);
    }, 3000);

    // Cleanup the timer
    return () => clearTimeout(timer);
  }, [id, token, navigate]);

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-lg">
        <h1 className="text-3xl font-bold mb-4">Payment Successful!</h1>
        <p className="text-lg mb-4">Your payment for listing {title} has been successfully completed.</p>
        <p>Thank you for your purchase.</p>
      </div>
    </div>
  );
};

export default PaymentSuccess;
