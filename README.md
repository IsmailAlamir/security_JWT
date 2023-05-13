# security_JWT

## summary of the components and their functionality:
1. **User** and **Role** : These classes represent the user entity and user roles in the application.

2. **UserRepository** : This interface extends the JpaRepository interface and provides methods for interacting with the user database.

3. **AuthenticationController** : This controller class handles authentication-related endpoints such as user registration and authentication.

4. **RegisterRequest** and **AuthenticateRequest** : These classes define the request payload structures for user registration and authentication.

5. **AuthenticationResponse** : This class defines the response payload structure for authentication operations, including the JWT token.

6. **AuthenticationService** : This service class handles user registration and authentication logic.

7. **ApplicationConfig** : This configuration class provides beans for user details service, authentication provider, password encoder, and authentication manager.

8. **JwtService** : This service class handles JWT-related operations, such as token generation, validation, and extraction of claims.

9. **DemoController** : This controller class contains a sample endpoint that requires authentication.

