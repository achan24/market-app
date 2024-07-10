import React, { useContext } from 'react';
import { Search, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthContext } from './AuthContext';

const Navbar = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { logout } = useContext(AuthContext);

  const login = () => {
    navigate('/login');
  }

  const logOut = () => {
    logout();
    navigate('/');
  }

  const placeAd = () => {
    if (isAuthenticated) navigate('/createListing');
  }

  const handleUsernameClick = () => {
    navigate(`/user/${user.username}`);
  }

  const toHome = () => {
    navigate('/')
  }

  return (
    <>
      {/* Header */}
      <header className="bg-white p-4 flex items-center justify-between">
        <div className="text-2xl font-bold text-green-600 cursor-pointer" onClick={toHome}>ReValue.ie</div>
        <div className="flex-grow mx-8">
          <div className="relative">
            <input
              type="text"
              placeholder="Search for anything"
              className="w-full p-2 border-2 border-black rounded-full pl-10"
            />
            <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
          </div>
        </div>
        <div className="flex items-center space-x-6">
          {isAuthenticated && (
            <div className='flex items-center space-x-3'>
              <button onClick={handleUsernameClick}>
                <h2 className='border border-gray-400 rounded-full px-4 py-2'>{user.username}</h2>
              </button>
              <div className="relative group">
                <LogOut className="cursor-pointer" onClick={logOut} />
                <span className="absolute invisible group-hover:visible bg-gray-800 text-white text-xs rounded py-1 px-2 top-full mt-1 left-1/2 transform -translate-x-1/2 whitespace-nowrap">
                  Logout
                </span>
              </div>
            </div>
          )}
          {!isAuthenticated && (
            <button
              onClick={login}
              className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
              Login or Signup
            </button>
          )}
          <button
            onClick={placeAd}
            className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
            Place Ad
          </button>
        </div>
      </header>

    </>
  );
};

export default Navbar;
