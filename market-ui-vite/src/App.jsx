import { useState, useContext } from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import Home from './components/Home'
import Register from './components/Login'
import Login from './components/Login'
import { Button } from './components/ui/button'
import { CarouselDemo } from './components/demo/CarouselDemo'
import { AuthProvider } from './components/AuthContext';
import Sell from './components/Sell'
import CreateListing from './components/CreateListing'
import PhotoUpload from './components/PhotoUpload'
import Header from './components/Header'
import CategorySelector from './components/CategorySelector'
import CardListing from './components/CardListing'
import ListingPage from './components/ListingPage'
import UserProfile from './components/UserProfile'
import Inbox from './components/Inbox'



function App() {

  const user = {
    username: 'johndoe',
    email: 'john@example.com',
    joinDate: '2023-01-01',
    itemsSold: 10,
    itemsPurchased: 5,
    positiveRatings: 95,
    activeListings: [
      { title: 'Vintage Lamp', price: 25 },
      { title: 'Bicycle', price: 100 },
    ],
    purchaseHistory: [
      { item: 'Bookshelf', price: 50 },
      { item: 'Coffee Maker', price: 30 },
    ],
  };

  const currentuser = "currentUser";
  const conversations = [
    {
      id: 1,
      avatar: "path_to_avatar.jpg",
      username: "zax4",
      lastMessage: "Iphone 13pro max",
      timeAgo: "2 months",
      lastActive: "5 days ago",
      acceptedOffer: 450,
      messages: [
        { sender: "zax4", content: "on the way", timestamp: "May 2nd, 2024 - 2:44pm" },
        { sender: currentuser, content: "No worries", timestamp: "May 2nd, 2024 - 2:46pm" },
        { sender: "zax4", content: "I'm here now", timestamp: "May 2nd, 2024 - 2:48pm" },
        { sender: "zax4", content: "scinic", timestamp: "May 2nd, 2024 - 2:48pm" },
      ]
    },
    // ... more conversations
  ];

  return (
    
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/sell" element={<Sell />} />
          <Route path='/createListing' element={<CreateListing />} />
          <Route path='/photoUpload' element={<PhotoUpload />} />
          <Route path='/header' element={<Header />} />
          <Route path='/category' element={<CategorySelector />} />
          <Route path='/card' element={<CardListing />} />
          <Route path='/listing/:id' element={<ListingPage />} />
          <Route path='/user/:user' element={<UserProfile />} />
          <Route path='/inbox' element={<Inbox />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
    


  )
}

export default App
