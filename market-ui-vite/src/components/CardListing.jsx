import React from 'react';
import { Link } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Heart, MapPin, Clock } from "lucide-react";
import { formatDistanceToNow } from 'date-fns'

const CardListing = (props) => {
  const data = props.product

  const firstImage = data.images && data.images.length > 0 ? `data:${data.images[0].fileType};base64,${data.images[0].data}` : null;
  
  const relativeTime = data.createdAt 
    ? formatDistanceToNow(new Date(data.createdAt), { addSuffix: true })
    : '?';

   const shortRelativeTime = relativeTime.startsWith("about ") 
    ? relativeTime.slice(6)
    : relativeTime


  return (
    <Link to={`/listing/${data.id}`}>
      <Card className="w-[300px] border border-gray-300">
        <CardHeader>
          {/* Probably change this afterwards */}
          <img src={firstImage!==null?firstImage:data.image} alt={data.title} className="w-full h-48 object-cover rounded-md border" />
          <CardTitle className="mt-4">{data.title}</CardTitle>
          <CardDescription>{data.description}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex justify-between items-center mb-4">
            <Badge variant="secondary" className="text-lg font-semibold">€{data.askingPrice}</Badge>
            <div className="flex items-center text-sm text-muted-foreground">
              <MapPin className="mr-1" size={16} />
              {data.location}
            </div>
          </div>
          <div className="flex items-center space-x-4">
            <Avatar>
              <AvatarImage src="https://github.com/shadcn.png" />
              <AvatarFallback>{data.sellerName}</AvatarFallback>
            </Avatar>
            <div>
              <p className="text-sm font-medium">{data.sellerName}</p>
              <p className="text-xs text-muted-foreground">Seller</p>
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button variant="outline">Contact Seller</Button>
          <div className="flex items-center space-x-2">
            {/* <Button variant="ghost" size="icon">
              <Heart className="h-4 w-4" />
            </Button> */}
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

export default CardListing