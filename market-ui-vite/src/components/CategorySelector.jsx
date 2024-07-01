import React, { useState } from 'react';

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

const CategorySelector = () => {
  const [selectedCategory, setSelectedCategory] = useState('');

  const handleCategoryChange = (event) => {
    setSelectedCategory(event.target.value);
  }

  return (
    <div className="space-y-4 p-4">
      <div>
        <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">
          Select a category
        </label>
        <select
          id="category"
          value={selectedCategory}
          onChange={handleCategoryChange}
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
        >
          <option value="">Choose a category</option>
          {categoryData.map((category, categoryIndex) => (
            <React.Fragment key={categoryIndex}>
              <option value="" disabled className="font-bold text-lg bg-gray-200 text-gray-800" style={{textTransform: 'uppercase'}}>
                {category.name}
              </option>
              {category.subcategories.map((subcategory, subcategoryIndex) => (
                <option key={`${categoryIndex}-${subcategoryIndex}`} value={`${category.name} - ${subcategory}`} className="pl-4">
                  {subcategory}
                </option>
              ))}
            </React.Fragment>
          ))}
        </select>
      </div>

      {selectedCategory && (
        <p className="mt-4 text-green-600">
          You've selected: {selectedCategory}
        </p>
      )}
    </div>
  );
};

export default CategorySelector;