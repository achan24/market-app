import React, { useState } from 'react';
import PhotoUpload from './PhotoUpload';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectGroup, SelectLabel, SelectItem} from './ui/select'
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';

const CreateListing = () => {
  const { token } = useAuth()
  const [listing, setListing] = useState({
    title: '',
    description: '',
    category: '',
    askingPrice: 0,
    location: '',
    //sellerId: 0 // This should probably come from your authentication context
  })
  const [selectedCategory, setSelectedCategory] = useState('')
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const navigate = useNavigate()
  

  const handleChange = (e) => {
    setListing({ ...listing, [e.target.name]: e.target.value })
  }

  const categoryData = [
    {
      name: "Motors",
      subcategories: ["Cars", "Motorcycles", "Trucks", "Boats"]
    },
    {
      name: "Electronics & Media",
      subcategories: ["Computers", "Phones", "TVs", "Cameras", "Other Electronics"]
    },
    {
      name: "Home & Living",
      subcategories: ["Furniture", "Home Decor", "Garden", "Appliances"]
    },
    {
      name: "Fashion & Beauty",
      subcategories: ["Women's Clothing", "Men's Clothing", "Jewelry", "Cosmetics"]
    }
  ]


  const handleCategoryChange = (event) => {
    setSelectedCategory(event)
    setListing({ ...listing, category: event })
  }


  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) {
      setError("You must be logged in to create a listing.");
      return;
    }
    try {
      const response = await fetch('http://localhost:8000/api/v1/listings', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(listing)
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      console.log('Listing created:', data);
      setSuccess(true);
      setError(null);
      
      returnHome()
    } catch (error) {
      console.error('There was an error creating the listing:', error);
      setError(error.message);
      setSuccess(false);
    }
  }

  const returnHome = () => {
    setTimeout(()=>{
      navigate('/')
    }, 1000)

  }

  return (
    <div className="max-w-md mx-auto mt-10 bg-white p-8 border border-gray-300 rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Create New Listing</h2>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="category" className="block text-sm font-medium text-gray-700">Category</label>
          
        <Select 
          id="category"
          name="category"
          value={selectedCategory} 
          onValueChange={handleCategoryChange}
        >
          <SelectTrigger className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm">
            <SelectValue placeholder="Select a category" />
          </SelectTrigger>
          <SelectContent>
            {categoryData.map((category) => (
              <SelectGroup key={category.name}>
                <SelectLabel>{category.name}</SelectLabel>
                {category.subcategories.map((subcategory) => (
                  <SelectItem key={subcategory} value={subcategory}>{subcategory}</SelectItem>
                ))}
              </SelectGroup>
            ))}
          </SelectContent>
        </Select>
        </div>
        <div>
          <label htmlFor="title" className="block text-sm font-medium text-gray-700">Title</label>
          <input
            type="text"
            id="title"
            name="title"
            value={listing.title}
            onChange={handleChange}
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            required
          />
        </div>
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">Description</label>
          <textarea
            id="description"
            name="description"
            value={listing.description}
            onChange={handleChange}
            rows="3"
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            required
          ></textarea>
        </div>
        
        <div>
          <label htmlFor="askingPrice" className="block text-sm font-medium text-gray-700">Asking Price (€)</label>
          <input
            type="number"
            id="askingPrice"
            name="askingPrice"
            value={listing.askingPrice}
            onChange={handleChange}
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            required
          />
        </div>
        <PhotoUpload />
        <div>
          <label htmlFor="location" className="block text-sm font-medium text-gray-700">Location</label>
          <input
            type="text"
            id="location"
            name="location"
            value={listing.location}
            onChange={handleChange}
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            required
          />
        </div>
        <div>
          <button
            type="submit"
            onClick={handleSubmit}
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Create Listing
          </button>
        </div>
      </form>
      {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
      {success && 
        (<div>
          <p className="text-green-500 text-sm mt-2 my-2">Listing created successfully!</p>
          <p className='text-xs'>Please wait while we return you to homepage...</p>
          </div>
        )}
    </div>
  );
};

export default CreateListing;