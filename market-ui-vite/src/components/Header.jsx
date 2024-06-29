import React, { useContext } from 'react'
import { Search, ShoppingBag, Heart, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthContext } from './AuthContext';

const Header = () => {
  return (
    <>
      <header className="bg-white p-2 shadow-sm">
        <div className="max-w-6xl mx-auto flex items-center justify-between space-x-4">
          <div className="text-2xl font-bold text-orange-500 whitespace-nowrap">ReValue.ie</div>
          
          <div className="flex-grow max-w-xl relative">
            <input
              type="text"
              placeholder="Search for anything"
              className="w-full p-2 border-2 border-black rounded-full pl-10"
            />
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
          </div>
          
  
            <div className="flex items-center whitespace-nowrap">
              <span className="mr-2">Test</span>
              <LogOut className="cursor-pointer" onClick={null}/>
            </div>
          
          <button 
            onClick={null}
            className="bg-orange-500 text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-orange-600 whitespace-nowrap"
          >
            Place Ad
          </button>
        </div>
      </header>
    </>
  )
}

export default Header