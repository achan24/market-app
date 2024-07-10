import React, { useState, useEffect } from 'react';
import { useAuth } from './AuthContext';
import { Mail, MapPin, Calendar, Save } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const UserProfile = () => {
  const { user: authUser, token } = useAuth();
  const [userDetails, setUserDetails] = useState({
    username: '',
    email: '',
    location: '',
    createdAt: null
  });
  const [isEditing, setIsEditing] = useState(false);
  const [editedDetails, setEditedDetails] = useState({
    email: '',
    location: ''
  });
  const navigate = useNavigate();

  useEffect(() => {
    console.log("UserProfile component: authUser object", authUser);

    const fetchUserDetails = async () => {
      if (!token) {
        console.error('No token found');
        return;
      }

      try {
        const response = await fetch(`http://localhost:8000/user/${authUser.username}`, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          console.log(data);
          setUserDetails(data);
        } else {
          console.error('Failed to fetch user details, status:', response.status);
        }
      } catch (error) {
        console.error('Error fetching user details:', error);
      }
    };

    if (authUser) {
      fetchUserDetails();
    }
  }, [authUser, token]);

  const handleEdit = () => {
    setIsEditing(true);
    setEditedDetails({
      email: userDetails.email || '',
      location: userDetails.location || ''
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditedDetails(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    try {
      const response = await fetch(`http://localhost:8000/user/${authUser.username}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          email: editedDetails.email,
          location: editedDetails.location
        })
      });

      if (response.ok) {
        setUserDetails(prev => ({
          ...prev,
          email: editedDetails.email,
          location: editedDetails.location
        }));
        setIsEditing(false);
      } else {
        console.error('Failed to update user details');
      }
    } catch (error) {
      console.error('Error updating user details:', error);
    }
  };

  const inboxPage = () => {
    navigate('/inbox');
  };

  if (!userDetails.username) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  const formattedDate = userDetails.createdAt ? new Date(userDetails.createdAt).toLocaleDateString() : 'N/A';

  return (
    <div className="container mx-auto p-4 font-sans bg-gray-100 min-h-screen">
      <div className="bg-white rounded-lg shadow-md p-8 mb-6">
        <div className="flex flex-col md:flex-row justify-between items-start mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 mb-2">{userDetails.username}</h1>
            <p className="text-gray-600 flex items-center">
              <MapPin size={18} className="mr-2" />
              {isEditing ? (
                <input
                  type="text"
                  name="location"
                  value={editedDetails.location}
                  onChange={handleChange}
                  className="border-b-2 border-gray-300 focus:outline-none focus:border-orange-500"
                />
              ) : (
                userDetails.location || 'Location not set'
              )}
            </p>
          </div>
          <div className="flex space-x-4 mt-4 md:mt-0">
            <button 
              className="px-4 py-2 bg-orange-500 text-white rounded-full hover:bg-orange-600 transition flex items-center"
              onClick={inboxPage}
            >
              <Mail size={18} className="mr-2" />
              Inbox
            </button>
            {isEditing ? (
              <button 
                onClick={handleSave}
                className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 transition flex items-center"
              >
                <Save size={18} className="mr-2" />
                Save
              </button>
            ) : (
              <button 
                onClick={handleEdit}
                className="px-4 py-2 border border-gray-300 rounded-full text-gray-700 hover:bg-gray-100 transition flex items-center"
              >
                Edit Profile
              </button>
            )}
          </div>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h2 className="text-xl font-semibold mb-3 text-gray-700">About</h2>
            <div className="flex items-center text-gray-600 mb-2">
              <Mail size={18} className="mr-2" />
              {isEditing ? (
                <input
                  type="email"
                  name="email"
                  value={editedDetails.email}
                  onChange={handleChange}
                  className="border-b-2 border-gray-300 focus:outline-none focus:border-orange-500"
                />
              ) : (
                userDetails.email
              )}
            </div>
            <div className="flex items-center text-gray-600">
              <Calendar size={18} className="mr-2" />
              Member since {formattedDate}
            </div>
          </div>
          
          <div>
            <h2 className="text-xl font-semibold mb-3 text-gray-700">Stats</h2>
            <div className="bg-gray-50 p-4 rounded-lg">
              <p className="text-gray-600 mb-2">
                This user hasn't set up their stats yet.
              </p>
              <p className="text-gray-600">
                Check back later for more information!
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
