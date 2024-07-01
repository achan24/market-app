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


function App() {

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
          
        </Routes>
      </BrowserRouter>
    </AuthProvider>
    


  )
}

export default App
