import React, { useContext } from 'react'
import { Search, ShoppingBag, Heart, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthContext } from './AuthContext';

const Home = () => {
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth();
  const { logout } = useContext(AuthContext);
  
  // Safe logging
  console.log(`Authentication status: ${isAuthenticated}`)
  if (user) {
    console.log(`User details: ${JSON.stringify(user)}`)
  } else {
    console.log('No user logged in')
  }
  
  const login = () => {
    navigate('/login')
  }


  const logOut = () => {
    
    logout()
    navigate('/')
  }

  const placeAd = () => {
    //check if user is logged in
    //or maybe only show if user is logged in
    if(isAuthenticated)
      navigate('/createListing')
  }

  return (
    <div className="font-sans">
      {/* Header */}
      <header className="bg-white p-4 flex items-center justify-between">
        <div className="text-2xl font-bold text-orange-500">ReValue.ie</div>
        <div className="flex-grow mx-4">
          <div className="relative">
            <input
              type="text"
              placeholder="Search for anything"
              className=" w-2/3 p-2 border-2 border-black rounded-full pl-10"
            />
            <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
          </div>
        </div>
        <div className="flex items-center space-x-4">
          {isAuthenticated && (
              <div className='flex items-center space-x-3'>
                <h2>{user.username}</h2>
                <div className="relative group">
                  <LogOut className="cursor-pointer" onClick={logOut}/>
                  <span className="absolute invisible group-hover:visible bg-gray-800 text-white text-xs rounded py-1 px-2 top-full mt-1 left-1/2 transform -translate-x-1/2 whitespace-nowrap">
                    Logout
                  </span>
                </div>
              </div>
            )
          }
          {!isAuthenticated &&
            <button 
              onClick={login}
              className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
              Login or Signup
            </button>
          }
          <button 
            onClick={placeAd}
            className="text-gray-700 border-2 border-black px-4 py-2 rounded-full hover:bg-gray-100">
            Place Ad
          </button>
          
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-gray-100 p-2 flex justify-center space-x-4">
        <button className="text-gray-700 hover:bg-gray-200 rounded-full px-4 py-2">Motors</button>
        <button className="text-gray-700 hover:bg-gray-200 rounded-full px-4 py-2">Electronics & Media</button>
        <button className="text-gray-700 hover:bg-gray-200 rounded-full px-4 py-2">Hobbies & Lifestyle</button>
        <button className="text-gray-700 hover:bg-gray-200 rounded-full px-4 py-2">Fashion & Beauty</button>
        <button className="text-gray-700 hover:bg-gray-200 rounded-full px-4 py-2">Home & Living</button>
        {/* <button className="text-gray-700">Gift Cards</button> */}
      </nav>

      {/* Main Content */}
      <main className="container mx-auto mt-8">
        <h1 className="text-3xl font-bold text-center mb-8">
          Shop from talented creators and curators!
        </h1>

        {/* Category Icons */}
        <div className="grid grid-cols-6 gap-4 mb-12">
          {['Back-to-School Savings', 'Birthday Gifts', 'Wedding Gifts', 'Home Gifts', 'Garden & Floral Gifts', 'Up to 30% Off'].map((category, index) => (
            <div key={index} className="text-center">
              <div className="w-24 h-24 bg-gray-200 rounded-full mx-auto mb-2"></div>
              <p className="text-sm">{category}</p>
            </div>
          ))}
        </div>

        {/* Popular Gifts */}
        <h2 className="text-2xl font-bold mb-4">Popular gifts right now</h2>
        <div className="grid grid-cols-5 gap-4">
          {[1, 2, 3, 4, 5].map((item) => (
            <div key={item} className="border rounded-lg p-4">
              <div className="bg-gray-200 h-40 mb-2 rounded"></div>
              <h3 className="font-semibold mb-1">Product Name</h3>
              <div className="flex items-center mb-1">
                <span className="text-yellow-400">★★★★★</span>
                <span className="text-sm text-gray-600 ml-1">(1,234)</span>
              </div>
              <p className="font-bold">€XX.XX</p>
              <p className="text-sm text-gray-600 line-through">€XX.XX</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Home;