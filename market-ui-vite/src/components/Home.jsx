import React, { useState, useEffect, useContext } from 'react'
import { Search, ShoppingBag, Heart, LogOut } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuth, AuthContext } from './AuthContext'
import CardListing from './CardListing'

const Home = () => {
  const navigate = useNavigate()
  const { user, isAuthenticated } = useAuth()
  const { logout } = useContext(AuthContext)

  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)

  const data = []
  const fetchData = async () => {
    try {
      const response = await fetch('http://localhost:8000/api/v1/listings')
      if(!response.ok) {
        throw new Error('Error fetching listings')
      }
      const data = await response.json()
      console.log(data) 
      return data   
    } catch(err) {
      console.log('Error fetching listings', err)
    }
  }
  // fetchData()
  
  useEffect(() => {
    const getProducts = async () => {
      try {
        const data = await fetchData()
        setProducts(data)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false);
      }
    }

    getProducts();
  }, []);
  
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

  const dummyData = [
    {
      title: "Vintage Wooden Coffee Table",
      askingPrice: "120",
      location: "San Francisco, CA",
      image: "https://i.etsystatic.com/10486522/r/il/d0dd93/1816412232/il_fullxfull.1816412232_g98w.jpg",
      description: "A beautifully crafted vintage wooden coffee table, perfect for adding a rustic touch to your living room. Dimensions: 48\" L x 24\" W x 18\" H.",
      seller: "John Doe",
      postedTime: "2 hours ago"
    },
    // Duplicate the same data or create variations
    {
      title: "Modern Art Piece",
      askingPrice: "250",
      location: "New York, NY",
      image: "https://doagahehoc242.cloudfront.net/uploads/posts/726/5040b61f_piet-mondrian-composition-c-noiii-with-red-yellow-and-blue-900.jpeg",
      description: "A vibrant modern art piece to elevate your interior decor. Dimensions: 36\" H x 24\" W.",
      seller: "Jane Smith",
      postedTime: "3 hours ago"
    },
    {
      title: "Antique Vase",
      askingPrice: "90",
      location: "Los Angeles, CA",
      image: "https://eg6dnc6a5nx.exactdn.com/wp-content/uploads/2020/07/1017-1.jpeg",
      description: "A delicate antique vase from the 19th century. Perfect for collectors.",
      seller: "Alice Brown",
      postedTime: "4 hours ago"
    },
    {
      title: "Handmade Rug",
      askingPrice: "300",
      location: "Chicago, IL",
      image: "https://classicworldrugs.com/cdn/shop/products/classic-world-rugs-cwral-3060-005_1024x1024@2x.jpg",
      description: "A beautiful handmade rug that adds warmth and comfort to any room.",
      seller: "Robert Johnson",
      postedTime: "5 hours ago"
    },
    {
      title: "Luxury Watch",
      askingPrice: "500",
      location: "Miami, FL",
      image: "https://globalboutique.com/wp-content/uploads/2023/05/featured-gold-watches-800x600.jpg",
      description: "A luxury watch that combines elegance and functionality. Perfect for any occasion.",
      seller: "Michael Williams",
      postedTime: "6 hours ago"
    }
  ];

  const handleUsernameClick = () => {
    console.log(user)
    console.log(user.username)
    navigate(`/user/${user.username}`)
  }
  

  return (
    <div className="font-sans">

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
        {/* <h1 className="text-3xl font-bold text-center mb-8">
          Shop from talented creators and curators!
        </h1>

        
        <div className="grid grid-cols-6 gap-4 mb-12">
          {['Back-to-School Savings', 'Birthday Gifts', 'Wedding Gifts', 'Home Gifts', 'Garden & Floral Gifts', 'Up to 30% Off'].map((category, index) => (
            <div key={index} className="text-center">
              <div className="w-24 h-24 bg-gray-200 rounded-full mx-auto mb-2"></div>
              <p className="text-sm">{category}</p>
            </div>
          ))}
        </div> */}


        {/* <h2 className="text-2xl font-bold mb-4">Static Dummy Data Card List</h2>
        <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {dummyData.map((product, index) => (
            <CardListing key={index} product={product} />
          ))}
        </div> */}

        {loading ?
          <div>Loading...</div> : (
          <div>
            <h2 className="text-2xl font-bold mb-4">Live Data Card List</h2>
            <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {products.map((product, index) => (
                <CardListing key={index} product={product} />
              ))}
            </div>
          </div>
          )
        }
      </main>
    </div>
  );
};

export default Home;