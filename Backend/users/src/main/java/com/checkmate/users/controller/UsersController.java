package com.checkmate.users.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.checkmate.users.model.dto.responses.UserProfileDTO;
import com.checkmate.users.model.dto.responses.UserSearchResultDTO;
import com.checkmate.users.model.entity.Following;
import com.checkmate.users.model.entity.Friendship;
import com.checkmate.users.model.entity.User;
import com.checkmate.users.model.entity.UserProfile;
import com.checkmate.users.repository.FollowingRepository;
import com.checkmate.users.repository.FriendshipRepository;
import com.checkmate.users.repository.UserRepository;
import com.checkmate.users.security.OptionalJWT;
import com.checkmate.users.security.PermissionRequired;
import com.checkmate.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final FollowingRepository followingRepository;


    public UsersController(UserService userService, UserRepository userRepository, FriendshipRepository friendshipRepository, FollowingRepository followingRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.followingRepository = followingRepository;

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String test(HttpServletRequest request) {
        logger.info("Request received from IP: {}", request.getRemoteAddr());
        logger.info("Test endpoint called");
        return "API is up and running!";
    }


    // REST API get mapping for /searchForUsers
    @GetMapping("/searchForUsers")
    public List<UserSearchResultDTO> searchForUsers(@RequestParam("searchQuery") String searchQuery) {
        List<User> userList = userService.searchForUser(searchQuery);

        List<UserSearchResultDTO> userSearchList = new ArrayList<>();

        for (User user : userList) {
            UserSearchResultDTO thisUser = new UserSearchResultDTO();
            thisUser.setUserId(user.getUserId());
            thisUser.setUsername(user.getUsername());
            thisUser.setDisplayName(user.getDisplayName());

            userSearchList.add(thisUser);
        }

        return userSearchList;

    }


    @GetMapping("/getUserProfile")
    @OptionalJWT
    public ResponseEntity<?> getUserProfile(@RequestParam("userId") Long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found.");
        }

        UserProfile userProfile = user.getUserProfile();

        if (userProfile == null) {
            return ResponseEntity.status(404).body("User profile not found.");
        }

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUserId(user.getUserId());
        userProfileDTO.setDisplayName(user.getDisplayName());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setBiography(userProfile.getUserBiography());

        // Number of people this user is following
        long followingCount = followingRepository.countByFollowerId(user.getUserId());
        userProfileDTO.setFollowingCount(followingCount);

        // Number of followers this user has
        long followerCount = followingRepository.countByFollowingId(user.getUserId());
        userProfileDTO.setFollowerCount(followerCount);

        // Number of friends this user has
        long friendCount = friendshipRepository.countAllRelationshipsByUserIdAndStatus(user.getUserId(), Friendship.FriendshipStatus.FRIENDS);
        userProfileDTO.setFriendCount(friendCount);

        // Check if the user is following the current user
        if (decodedJWT == null) {
            userProfileDTO.setFollowing(false);
            userProfileDTO.setFollower(false);
            userProfileDTO.setFriends(false);
            userProfileDTO.setFriendRequestSent(false);
            userProfileDTO.setRequestingFriendship(false);
            return ResponseEntity.ok().body(userProfileDTO);
        }

        long clientUserId = decodedJWT.getClaim("userId").asLong();

        if (clientUserId == user.getUserId()) {
            userProfileDTO.setOwnProfile(true);
            userProfileDTO.setFollowing(false);
            userProfileDTO.setFollower(false);
            userProfileDTO.setFriends(false);
            userProfileDTO.setFriendRequestSent(false);
            userProfileDTO.setRequestingFriendship(false);
            return ResponseEntity.ok().body(userProfileDTO);
        }

        Following following = followingRepository.findByFollowerIdAndFollowingId(clientUserId, user.getUserId());
        if (following != null) {
            userProfileDTO.setFollowing(true);
        } else {
            userProfileDTO.setFollowing(false);
        }

        Following follower = followingRepository.findByFollowerIdAndFollowingId(user.getUserId(), clientUserId);
        if (follower != null) {
            userProfileDTO.setFollower(true);
        } else {
            userProfileDTO.setFollower(false);
        }

        Friendship clientToUser = friendshipRepository.findByRequesterIdAndReceiverId(clientUserId, user.getUserId());
        Friendship userToClient = friendshipRepository.findByRequesterIdAndReceiverId(user.getUserId(), clientUserId);

        if (clientToUser == null && userToClient == null) {
            userProfileDTO.setFriends(false);
        } else {
            if (clientToUser != null) {
                if (clientToUser.getStatus() == Friendship.FriendshipStatus.PENDING) {
                    userProfileDTO.setFriendRequestSent(true);
                    userProfileDTO.setRequestingFriendship(false);
                    userProfileDTO.setFriends(false);
                } else {
                    userProfileDTO.setFriends(true);
                    userProfileDTO.setFriendRequestSent(false);
                    userProfileDTO.setRequestingFriendship(false);
                }
            }

            if (userToClient != null) {
                if (userToClient.getStatus() == Friendship.FriendshipStatus.PENDING) {
                    userProfileDTO.setRequestingFriendship(true);
                    userProfileDTO.setFriendRequestSent(false);
                    userProfileDTO.setFriends(false);
                } else {
                    userProfileDTO.setFriends(true);
                    userProfileDTO.setFriendRequestSent(false);
                    userProfileDTO.setRequestingFriendship(false);
                }
            }
        }

        return ResponseEntity.ok().body(userProfileDTO);
    }

    private String numberToFormattedString(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1000000) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number < 1000000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else {
            return String.format("%.1fB", number / 1000000000.0);
        }
    }
}
