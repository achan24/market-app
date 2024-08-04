import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Heart, MapPin, Clock } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';

const fetchUserProfile = async (sellerUsername) => {
  try {
    const response = await fetch(`http://localhost:8000/user/profile-pic/${sellerUsername}`);
    if (response.ok) {
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      return url;
    } else if (response.status === 404) {
      // Handle 404 silently by returning a default or null
      return null;
    } else {
      // Handle other HTTP errors if needed
      return null;
    }
  } catch (error) {
    // Handle network errors or unexpected issues silently
    return null;
  }
};

const CardListing = (props) => {
  const data = props.product;
  const [profilePic, setProfilePic] = useState(null);

  useEffect(() => {
    const getUserProfilePic = async () => {
      const pic = await fetchUserProfile(data.sellerName);
      setProfilePic(pic);
    };

    getUserProfilePic();
  }, [data.sellerName]);

  const firstImage = data.images && data.images.length > 0
    ? `data:${data.images[0].fileType};base64,${data.images[0].data}`
    : null;

  const relativeTime = data.createdAt
    ? formatDistanceToNow(new Date(data.createdAt), { addSuffix: true })
    : '?';

  const shortRelativeTime = relativeTime.startsWith('about ')
    ? relativeTime.slice(6)
    : relativeTime;

  const sellerProfilePic = profilePic
    ? profilePic
    : 'https://github.com/shadcn.png'; // Same fallback image

  return (
    <Link to={`/listing/${data.id}`}>
      <Card className="w-[300px] border border-gray-300 transition-shadow duration-150 hover:shadow-lg hover:shadow-[0_4px_10px_rgba(0,0,0,0.5)]">
        <CardHeader>
          <img
            src={firstImage !== null ? firstImage : data.image}
            alt={data.title}
            className="w-full h-48 object-cover rounded-md border"
          />
          <CardTitle className="mt-4">{data.title}</CardTitle>
          <CardDescription>{data.description}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex justify-between items-center mb-4">
            <Badge variant="secondary" className="text-lg font-semibold">
              €{data.askingPrice}
            </Badge>
            <div className="flex items-center text-sm text-muted-foreground">
              <MapPin className="mr-1" size={16} />
              {data.location}
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <Avatar>
              <AvatarImage src={sellerProfilePic} />
              <AvatarFallback>
                <img src="https://github.com/shadcn.png" alt="Fallback" />  
              </AvatarFallback>
            </Avatar>
            <div>
              <p className="text-sm font-medium">{data.sellerName}</p>
              <p className="text-xs text-muted-foreground">Seller</p>
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex justify-between">
          <div className="flex items-center space-x-2">
            <div className="flex items-center text-xs text-muted-foreground">
              <Clock className="mr-1" size={14} />
              {data.createdAt && <span>{shortRelativeTime}</span>}
            </div>
          </div>
        </CardFooter>
      </Card>
    </Link>
  );
};

export default CardListing;
