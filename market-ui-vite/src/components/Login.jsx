import React, {useState} from 'react'
import { Input } from './ui/input'
import { Button } from './ui/button'
import { Mail } from "lucide-react"
import { useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'


const Register = () => {

  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isDisabled, setIsDisabled] = useState(false)
  const [showPopup, setShowPopup] = useState(false)
  const navigate = useNavigate()
  const { login } = useAuth()
  const [registerMode, setRegisterMode] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const [showErrorMessage, setShowErrorMessage] = useState(false)
  

  const handleSubmit = async (event) => {
    event.preventDefault(); // Prevent the form from submitting the traditional way
    
    setIsDisabled(true); // Disable the button immediately when clicked
    
    //check what mode it's in first

    if(!registerMode) {
      //Login
      loggingIn()
      

      return
    }

    try {
      // First, register the user
      const registerResponse = await fetch('http://localhost:8000/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
      });
  
      if (registerResponse.ok) {
        console.log('Registration successful');
        
        loggingIn()

        // // Now, automatically log in the user
        // const loginResponse = await fetch('http://localhost:8000/auth/login', {
        //   method: 'POST',
        //   headers: {
        //     'Content-Type': 'application/json'
        //   },
        //   body: JSON.stringify({ username, password })
        // });
  
        // const loginData = await loginResponse.json()
        
        // if (loginResponse.ok) {
        //   //console.log('Login successful:', loginData);
          
        //   // Extract necessary user information
        //   const userData = {
        //     username: loginData.user.username,
        //     authorities: loginData.user.authorities,
        //     enabled: loginData.user.enabled,
        //     credentialsNonExpired: loginData.user.credentialsNonExpired,
        //     accountNonExpired: loginData.user.accountNonExpired,
        //     accountNonLocked: loginData.user.accountNonLocked
        //   };
        //   console.log('Login successful:', userData);
  
        //   // Call the login function from AuthContext with user data and JWT
        //   login(userData, loginData.jwt);
          
        //   setShowPopup(true)
        //   setTimeout(() => {
        //     navigate('/')
        //   }, 2500);
        // } else {
        //   throw new Error('Login failed after registration')
        // }
      } else {
        const errorData = await registerResponse.json();
        throw new Error(errorData.message || 'Registration failed')
      }
    } catch (error) {
      console.error('Registration/Login failed:', error)
      // Display error to the user
      // Handling an existing user
      console.log(error)
      console.log(error.message)
    } finally {
      setIsDisabled(false)
    }

    
  }

   

  const loggingIn = async () => {
    try {
        const loginResponse = await fetch('http://localhost:8000/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        console.log(loginResponse)
        
        if (!loginResponse.ok) {
            const errorData = await loginResponse.json();
            throw new Error(errorData.message || "Unknown error");
        }

        const loginData = await loginResponse.json();
        console.log('Login successful:', loginData);

        // Extract necessary user information
        const userData = {
            username: loginData.user.username,
            authorities: loginData.user.authorities,
            enabled: loginData.user.enabled,
            credentialsNonExpired: loginData.user.credentialsNonExpired,
            accountNonExpired: loginData.user.accountNonExpired,
            accountNonLocked: loginData.user.accountNonLocked
        };

        // Call the login function from AuthContext with user data and JWT
        login(userData, loginData.jwt);
        
        setShowPopup(true);
        setTimeout(() => {
          navigate('/');
        }, 1500);

    } catch(error) {
        console.error('Login error:', error.message);
        console.log('Please check your credentials and try again.');
        // Handle more specific error messages if necessary
        setErrorMessage(error.message)
        setShowErrorMessage(true);
        setTimeout(() => {
          setShowErrorMessage(false);
        }, 2500);
    } finally {
        setIsDisabled(false);
    }
  }


  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
  <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-lg">
    <h1 className='mb-12 text-4xl font-bold text-center py-2'>{registerMode ? 'Register' : 'Login'}</h1>
    <div className="flex flex-col items-center">
      <Input 
        className="w-full max-w-xs mb-8" 
        type="text"
        value={username}
        onChange={e => setUsername(e.target.value)}
        placeholder="Username"
      />
      <Input 
        className="w-full max-w-xs mb-8" 
        type="password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        placeholder="Password"
      />
      <div className='flex justify-center space-x-4 mb-8'>
        <Button 
          className="px-6 py-4 mx-4 text-white bg-blue-800 rounded-lg"
          onClick={handleSubmit}
          disabled={isDisabled}
        >
          {registerMode ? 'Register' : 'Login'}
        </Button> 
        <Button 
          className="px-6 py-4 mx-4 text-white bg-blue-800 rounded-lg"
          onClick={()=>
            {
              setEmail('')
              setPassword('')
              navigate('/')
            }
          }
        >
          Cancel
        </Button> 
      </div>
      {!registerMode && (
        <p className='text-sm'>
          Not a member? &nbsp;
          <a href='' 
            className='text-blue-500 hover:font-bold' 
            onClick={(e) => {
              e.preventDefault()
              setRegisterMode(true)
            }}
          >
            Sign up
          </a>
        </p>
      )}
    </div>
    {showPopup && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                <div className="bg-white p-4 rounded-lg shadow-lg px-16 py-16">
                    <p className='text-lg my-5'>{username}, you have successfully {registerMode ? 'registered!' : 'logged in!'}</p>
                    <p className='text-xs'>Please wait while we return you to the home page...</p>
                </div>
            </div>
          )}
          {showErrorMessage && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                <div className="bg-white p-4 rounded-lg shadow-lg px-16 py-16">
                    <p className='text-lg my-5'>{errorMessage}</p>
                    
                </div>
            </div>
          )}
  </div>
</div>



  )
}

export default Register