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
import Header from './components/Navbar'
import CategorySelector from './components/CategorySelector'
import CardListing from './components/CardListing'
import ListingPage from './components/ListingPage'
import UserProfile from './components/UserProfile'
import Inbox from './components/Inbox'
import PaymentForm from './components/PaymentForm'
import PaymentSuccess from './components/PaymentSuccess'
import Navbar from './components/Navbar'
import SearchPage from './components/SearchPage'
import ImageAnalysis from './components/ImageAnalysis'



function App() {

  return (
    
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/sell" element={<Sell />} />
          <Route path='/createListing' element={<CreateListing />} />
          <Route path='/photoUpload' element={<PhotoUpload />} />
          <Route path='/category' element={<CategorySelector />} />
          <Route path='/card' element={<CardListing />} />
          <Route path='/listing/:id' element={<ListingPage />} />
          <Route path='/user/:user' element={<UserProfile />} />
          <Route path='/inbox' element={<Inbox />} />
          <Route path='/payment/:id' element={<PaymentForm />} />
          <Route path='/payment-success/:id' element={<PaymentSuccess />} />
          <Route path='/search/' element={<SearchPage />} />
          <Route path='/vision/' element={<ImageAnalysis />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
    


  )
}

export default App
