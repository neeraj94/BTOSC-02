
import requests
import json

# Base URL for the application
BASE_URL = "http://0.0.0.0:8080"

def test_login():
    """Test the authentication endpoint"""
    login_url = f"{BASE_URL}/api/auth/signin"
    
    # Login credentials from the Postman collection
    login_data = {
        "username": "superadmin",
        "password": "SuperAdmin@123"
    }
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        print("Testing Authentication Endpoint...")
        print(f"URL: {login_url}")
        print(f"Payload: {json.dumps(login_data, indent=2)}")
        
        response = requests.post(login_url, json=login_data, headers=headers)
        
        print(f"\nResponse Status: {response.status_code}")
        print(f"Response Headers: {dict(response.headers)}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"Response Body: {json.dumps(response_data, indent=2)}")
            
            # Extract token for further testing
            token = response_data.get('token')
            if token:
                print(f"\n✓ Authentication successful!")
                print(f"JWT Token: {token[:50]}...")
                return token
        else:
            print(f"Response Body: {response.text}")
            print(f"✗ Authentication failed!")
            
    except requests.exceptions.ConnectionError:
        print("✗ Connection failed! Make sure the Spring Boot application is running on port 8080")
    except Exception as e:
        print(f"✗ Error occurred: {str(e)}")
    
    return None

def test_get_users(token):
    """Test the get users endpoint with authentication"""
    if not token:
        print("No token available for testing user endpoint")
        return
        
    users_url = f"{BASE_URL}/api/users"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    try:
        print(f"\n\nTesting Get Users Endpoint...")
        print(f"URL: {users_url}")
        
        response = requests.get(users_url, headers=headers)
        
        print(f"Response Status: {response.status_code}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"✓ Users retrieved successfully!")
            print(f"Total users: {response_data.get('totalElements', 'N/A')}")
            print(f"Current page: {response_data.get('number', 'N/A')}")
        else:
            print(f"Response Body: {response.text}")
            print(f"✗ Failed to retrieve users!")
            
    except Exception as e:
        print(f"✗ Error occurred: {str(e)}")

if __name__ == "__main__":
    # Test authentication first
    token = test_login()
    
    # Test authenticated endpoint
    test_get_users(token)
