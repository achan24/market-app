import React, { useState } from 'react';
import PhotoUpload from './PhotoUpload';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectGroup, SelectLabel, SelectItem} from './ui/select'
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';
import categories from './Categories';

const CreateListing = () => {
  const { token } = useAuth()
  const [listing, setListing] = useState({
    title: '',
    description: '',
    category: '',
    askingPrice: 0,
    location: '',
  })
  const [selectedCategory, setSelectedCategory] = useState('')
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const navigate = useNavigate()
  
  const handleChange = (e) => {
    setListing({ ...listing, [e.target.name]: e.target.value })
  }

  const handleFilesSelect = (newFiles) => {
    setSelectedFiles(newFiles);
  };

  
  const categoryData = Object.keys(categories).map(key => ({
    name: key,
    subcategories: categories[key]
  }));


  const handleCategoryChange = (event) => {
    setSelectedCategory(event)
    setListing({ ...listing, category: event })
  }

  const handleVisionAnalysis = async () => {
    if (selectedFiles.length === 0) {
      setError("Please upload at least one photo before analyzing.");
      return;
    }
  
    setIsAnalyzing(true);
    setError(null);
  
    const formData = new FormData();
    formData.append('image', selectedFiles[0].file);
  
    try {
      const response = await fetch('http://localhost:8000/vision/analyse', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      const data = await response.json();
      
      console.log('Vision API Response:', data);
  
      if (data.suggestedCategory) {
        setSelectedCategory(data.suggestedCategory.category);
        setListing(prev => ({ 
          ...prev, 
          category: data.suggestedCategory.category 
        }));
        setSuccess(`Suggested category: ${data.suggestedCategory.category} (${data.suggestedCategory.heading})`);
      } else {
        setError("Couldn't determine a category. Please select one manually.");
      }
    } catch (error) {
      console.error('Error analyzing image:', error);
      setError('Failed to analyze image. Please try again or select a category manually.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!token) {
      setError("You must be logged in to create a listing.");
      return;
    }

    const formData = new FormData();
    formData.append('listing', new Blob([JSON.stringify(listing)], { type: 'application/json' }));
    selectedFiles.forEach((file, index) => {
      formData.append(`images`, file.file);
    });

    try {
      const response = await fetch('http://localhost:8000/api/v1/listings', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
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

        <PhotoUpload selectedFiles={selectedFiles} onFilesSelect={handleFilesSelect} />
        <button
          type="button"
          onClick={handleVisionAnalysis}
          disabled={isAnalyzing || selectedFiles.length === 0}
          className="mt-2 w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
        >
          {isAnalyzing ? 'Analyzing...' : 'Categorise with Google Vision'}
        </button>
        
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
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Create Listing
          </button>
        </div>
      </form>
      {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
      {success && 
        (<div>
          <p className="text-green-500 text-sm mt-2 my-2">
            {typeof success === 'string' ? success : 'Listing created successfully!'}
          </p>
          {typeof success !== 'string' && <p className='text-xs'>Please wait while we return you to homepage...</p>}
        </div>
      )}
    </div>
  );
};

export default CreateListing;