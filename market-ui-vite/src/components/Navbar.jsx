import React, { useContext, useState, useEffect } from 'react';
import { Search, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthContext } from './AuthContext';
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

const Navbar = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated, token } = useAuth();
  const { logout } = useContext(AuthContext);
  const [searchQuery, setSearchQuery] = useState('');
  const [userDetails, setUserDetails] = useState(null);

  useEffect(() => {
    const fetchUserDetails = async () => {
      if (!user || !user.username) return;

      try {
        const response = await fetch(`http://localhost:8000/user/${user.username}`, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          setUserDetails(data);
        } else {
          console.error('Failed to fetch user details, status:', response.status);
        }
      } catch (error) {
        console.error('Error fetching user details:', error);
      }
    };

    if (isAuthenticated) {
      fetchUserDetails();
    }
  }, [isAuthenticated, user]);

  const login = () => {
    navigate('/login');
  };

  const logOut = () => {
    logout();
    navigate('/');
  };

  const placeAd = () => {
    if (isAuthenticated) navigate('/createListing');
  };

  const handleUsernameClick = () => {
    navigate(`/user/${user.username}`);
  };

  const toHome = () => {
    navigate('/');
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?query=${searchQuery}`);
    }
  };

  return (
    <>
      <header className="bg-white p-4 flex items-center justify-between">
        <div className="text-2xl font-bold text-green-600 cursor-pointer" onClick={toHome}>
          ReValue.ie
        </div>
        <div className="flex-grow mx-8">
          <form className="relative" onSubmit={handleSearchSubmit}>
            <input
              type="text"
              placeholder="Search for anything"
              className="w-full p-2 border-2 border-black rounded-full pl-10"
              value={searchQuery}
              onChange={handleSearchChange}
            />
            <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
          </form>
        </div>
        <div className="flex items-center space-x-6">
          {isAuthenticated ? (
            <div className="flex items-center space-x-3">
              <button onClick={handleUsernameClick} className="flex items-center space-x-2 border border-gray-400 rounded-full px-2 py-1">
                {userDetails && userDetails.profilePic ? (
                  <img
                    src={`data:image/jpeg;base64,${userDetails.profilePic}`}
                    alt="Profile"
                    className="w-8 h-8 rounded-full"
                  />
                ) : (
                  <Avatar>
                    <AvatarFallback>{user.username.charAt(0)}</AvatarFallback>
                  </Avatar>
                )}
                <h2>{user.username}</h2>
              </button>
              <div className="relative group">
                <LogOut className="cursor-pointer" onClick={logOut} />
                <span className="absolute invisible group-hover:visible bg-gray-800 text-white text-xs rounded py-1 px-2 top-full mt-1 left-1/2 transform -translate-x-1/2 whitespace-nowrap">
                  Logout
                </span>
              </div>
            </div>
          ) : (
            <button onClick={login} className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
              Login or Signup
            </button>
          )}
          <button onClick={placeAd} className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
            Place Ad
          </button>
        </div>
      </header>
    </>
  );
};

export default Navbar;
