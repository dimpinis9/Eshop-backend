package gr.mindthecode.eshop.controller;

import gr.mindthecode.eshop.dto.UserDTO;
import gr.mindthecode.eshop.payload.AuthenticationRequest;
import gr.mindthecode.eshop.payload.AuthenticationResponse;
import gr.mindthecode.eshop.service.impl.CustomUserDetailService;
import gr.mindthecode.eshop.service.impl.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eshop")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }catch (DisabledException e)
        {
            throw new Exception("USER_DISABLED",e);
        }
        catch(BadCredentialsException e){
            throw new Exception("INVALID_CREDENTIALS",e);
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(userDetailService.save(user));
    }

    @GetMapping("/role")
    public String getRole(){
        return userDetailService.getRole();
    }
}
