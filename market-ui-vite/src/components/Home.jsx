import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth, AuthContext } from './AuthContext';
import CardListing from './CardListing';
import categories from './Categories';
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  navigationMenuTriggerStyle,
} from '@/components/ui/navigation-menu';

const Home = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { logout } = useContext(AuthContext);

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      const response = await fetch('http://localhost:8000/api/v1/listings');
      if (!response.ok) {
        throw new Error('Error fetching listings');
      }
      const data = await response.json();
      return data;
    } catch (err) {
      console.error('Error fetching listings', err);
    }
  };

  useEffect(() => {
    const getProducts = async () => {
      try {
        const data = await fetchData();
        setProducts(data);
      } catch (err) {
        console.error(err.message);
      } finally {
        setLoading(false);
      }
    };

    getProducts();
  }, []);

  const slides = [
    { image: 'https://i.postimg.cc/NMgTbKhr/Furniture3.jpg', text: 'Breathing new life into pre-loved items' },
    { image: 'https://i.postimg.cc/ydV2QP92/Jars-Clocks.jpg', text: "Yesterday's gems are tomorrow's treasures" },
    { image: 'https://i.postimg.cc/4dBqr9Qy/Reimagine.jpg', text: 'Reimagine, Reuse, Revalue' },
  ];

  const ListItem = React.forwardRef(
    ({ className, title, ...props }, ref) => {
      return (
        <li>
          <NavigationMenuLink asChild>
            <a
              ref={ref}
              className={`block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground ${className}`}
              {...props}
            >
              <div className="text-sm font-medium leading-none">{title}</div>
            </a>
          </NavigationMenuLink>
        </li>
      );
    }
  );
  ListItem.displayName = 'ListItem';

  const handleCategoryClick = (category) => {
    navigate(`/search?category=${category}`);
  };

  return (
    <div className="font-sans">
      {/* Navigation */}
      <nav className="bg-gray-100 p-2 flex justify-center space-x-4">
        <NavigationMenu>
          <NavigationMenuList>
            {Object.keys(categories).map((category, index) => (
              <NavigationMenuItem key={index}>
                <NavigationMenuTrigger className={navigationMenuTriggerStyle()}>
                  {category}
                </NavigationMenuTrigger>
                {categories[category].length > 0 && (
                  <NavigationMenuContent>
                    <ul className="grid gap-3 p-4 w-[200px] sm:w-[300px] md:w-[400px]">
                      {categories[category].map((item, index) => (
                        <ListItem
                          key={index}
                          title={item}
                          onClick={() => handleCategoryClick(item)}
                        />
                      ))}
                    </ul>
                  </NavigationMenuContent>
                )}
              </NavigationMenuItem>
            ))}
          </NavigationMenuList>
        </NavigationMenu>
      </nav>

      {/* Hero Carousel */}
      <div className="carousel w-full h-80 mb-8 relative">
        {slides.map((slide, index) => (
          <div id={`slide${index + 1}`} className="carousel-item relative w-full h-full" key={index}>
            <img src={slide.image} className="w-full object-cover h-full" alt={`Slide ${index + 1}`} />
            <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2">
              <h2
                className="text-5xl font-extrabold text-center text-white"
                style={{
                  textShadow: '3px 3px 6px #000, -3px -3px 6px #000, 3px -3px 6px #000, -3px 3px 6px #000',
                }}
              >
                {slide.text}
              </h2>
            </div>
            <div className="absolute left-5 right-5 top-1/2 flex -translate-y-1/2 transform justify-between">
              <a href={`#slide${index === 0 ? slides.length : index}`} className="btn btn-circle">
                ❮
              </a>
              <a href={`#slide${index === slides.length - 1 ? 1 : index + 2}`} className="btn btn-circle">
                ❯
              </a>
            </div>
          </div>
        ))}
      </div>

      {/* Main Content */}
      <main className="container mx-auto mt-8">
        {loading ? (
          <div>Loading...</div>
        ) : (
          <div>
            {/* <h2 className="text-2xl font-bold mb-4">Live Data Card List</h2> */}
            <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {products.map((product, index) => (
                <CardListing
                  key={index}
                  product={product}
                  className="transform transition-transform duration-200 hover:scale-105 hover:shadow-lg hover:border-gray-400"
                />
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Home;
