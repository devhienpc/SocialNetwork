package com.checkmate.users.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.checkmate.users.model.dto.entity.FriendDTO;
import com.checkmate.users.model.dto.entity.UserDTO;
import com.checkmate.users.model.dto.requests.ChangeDisplayNameRequestDTO;
import com.checkmate.users.model.dto.responses.UserContextResponseDTO;
import com.checkmate.users.model.entity.Following;
import com.checkmate.users.model.entity.Friendship;
import com.checkmate.users.model.entity.User;
import com.checkmate.users.repository.FollowingRepository;
import com.checkmate.users.repository.FriendshipRepository;
import com.checkmate.users.repository.UserRepository;
import com.checkmate.users.security.PermissionRequired;
import com.checkmate.users.security.RequiresJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/relationship")
public class RelationshipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private FollowingRepository followingRepository;

    @GetMapping("/getFriends")
    @RequiresJWT
    public ResponseEntity<?> getFriends(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        long userId = decodedJWT.getClaim("userId").asLong();
        List<Friendship> friendshipList = friendshipRepository.getFriendsByUserIdAndStatus(userId, Friendship.FriendshipStatus.FRIENDS);

        List<FriendDTO> friendsList = new ArrayList<>();

        for (Friendship friendship : friendshipList) {
            FriendDTO friendDTO = new FriendDTO();
            long friendUserId = friendship.getRequesterId() == userId ? friendship.getReceiverId() : friendship.getRequesterId();
            Optional<User> optionalUser = userRepository.findByCredentialId(friendUserId);
            if (optionalUser.isPresent()) {
                User friendUser = optionalUser.get();
                friendDTO.setUserId(friendUser.getUserId());
                friendDTO.setUsername(friendUser.getUsername());
                friendDTO.setDisplayName(friendUser.getDisplayName());
                friendsList.add(friendDTO);
            }
        }

        return ResponseEntity.ok().body(friendsList);
    }

    @GetMapping("/getSentFriendRequests")
    @RequiresJWT
    public ResponseEntity<?> getSentFriendRequests(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        long userId = decodedJWT.getClaim("userId").asLong();

        List<Friendship> sentFriendRequests = friendshipRepository.findByRequesterIdAndStatus(userId, Friendship.FriendshipStatus.PENDING);
        List<UserDTO> userDTOList = new ArrayList<>();

        for (Friendship friendship : sentFriendRequests) {
            UserDTO userDTO = new UserDTO();
            Optional<User> optionalUser = userRepository.findByCredentialId(friendship.getReceiverId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setDisplayName(user.getDisplayName());
                userDTOList.add(userDTO);
            }
        }

        return ResponseEntity.ok().body(userDTOList);
    }

    @GetMapping("/getReceivedFriendRequests")
    @RequiresJWT
    public ResponseEntity<?> getReceivedFriendRequests(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        long userId = decodedJWT.getClaim("userId").asLong();

        List<Friendship> recievedFriendRequests = friendshipRepository.findByReceiverIdAndStatus(userId, Friendship.FriendshipStatus.PENDING);
        List<UserDTO> userDTOList = new ArrayList<>();

        for (Friendship friendship : recievedFriendRequests) {
            UserDTO userDTO = new UserDTO();
            Optional<User> optionalUser = userRepository.findByCredentialId(friendship.getRequesterId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setDisplayName(user.getDisplayName());
                userDTOList.add(userDTO);
            }
        }

        return ResponseEntity.ok().body(userDTOList);
    }

    @GetMapping("/getFollowers")
    @RequiresJWT
    public ResponseEntity<?> getFollowers(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        long userId = decodedJWT.getClaim("userId").asLong();

        List<Following> followerList = followingRepository.findByFollowingId(userId);

        List<UserDTO> userDTOList = new ArrayList<>();

        for (Following following : followerList) {
            UserDTO userDTO = new UserDTO();
            Optional<User> optionalUser = userRepository.findByCredentialId(following.getFollowerId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setDisplayName(user.getDisplayName());
                userDTOList.add(userDTO);
            }
        }


        return ResponseEntity.ok().body(userDTOList);
    }

    @GetMapping("/getFollowing")
    @RequiresJWT
    public ResponseEntity<?> getFollowingUsers(HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        long userId = decodedJWT.getClaim("userId").asLong();

        List<Following> followingList = followingRepository.findByFollowerId(userId);

        List<UserDTO> userDTOList = new ArrayList<>();

        for (Following following : followingList) {
            UserDTO userDTO = new UserDTO();
            Optional<User> optionalUser = userRepository.findByCredentialId(following.getFollowingId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                userDTO.setUserId(user.getUserId());
                userDTO.setUsername(user.getUsername());
                userDTO.setDisplayName(user.getDisplayName());
                userDTOList.add(userDTO);
            }
        }


        return ResponseEntity.ok().body(userDTOList);
    }


    @PostMapping("/blockUser")
    @RequiresJWT
    @Transactional
    public ResponseEntity<?> blockUser(@RequestParam("userId") long userId, HttpServletRequest request) {
        return null;
    }

    @PostMapping("/sendFriendRequest")
    @RequiresJWT
    @Transactional
    public ResponseEntity<?> sendFriendRequest(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        Long requesterId = decodedJWT.getClaim("userId").asLong();

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == requesterId) {
            return ResponseEntity.status(400).body("You cannot send a friend request to yourself");
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User userToRequest = optionalUser.get();

        // Check if user is already friends with the user
        Friendship requesterToRequested = friendshipRepository.findByRequesterIdAndReceiverId(requesterId, userId);
        Friendship requestedToRequester = friendshipRepository.findByRequesterIdAndReceiverId(userId, requesterId);

        // Friendship request exists from requested to requester, check if it's pending
        if (requestedToRequester != null) {
            if (requestedToRequester.getStatus() == Friendship.FriendshipStatus.PENDING) {
                // Friendship request is pending, accept the request
                requestedToRequester.setStatus(Friendship.FriendshipStatus.FRIENDS);

                friendshipRepository.save(requestedToRequester);

                return ResponseEntity.ok().body("Friend request accepted");
            } else {
                return ResponseEntity.status(400).body("You are already friends with this user");
            }
        }

        // Friendship request exists from requester to requested, check if it's pending
        if (requesterToRequested != null) {
            if (requesterToRequested.getStatus() == Friendship.FriendshipStatus.PENDING) {
                return ResponseEntity.status(400).body("Friend request already sent");
            } else {
                return ResponseEntity.status(400).body("You are already friends with this user");
            }
        }

        // Friendship request does not exist, create a new one

        Friendship friendship = new Friendship();

        friendship.setRequesterId(requesterId);
        friendship.setReceiverId(userId);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);

        friendshipRepository.save(friendship);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancelFriendRequest")
    @RequiresJWT
    public ResponseEntity<?> cancelFriendRequest(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        Long clientId = decodedJWT.getClaim("userId").asLong();

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == clientId) {
            return ResponseEntity.status(400).body("You cannot cancel a friend request to yourself");
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User user = optionalUser.get();

        // Check if user is already friends with the user
        Friendship requestedToRequester = friendshipRepository.findByRequesterIdAndReceiverId(userId, clientId);

        // Friendship request exists from requested to requester
        if (requestedToRequester != null) {
            // Friendship request is pending, delete the request
            if (requestedToRequester.getStatus() == Friendship.FriendshipStatus.PENDING) {
                friendshipRepository.delete(requestedToRequester);
                return ResponseEntity.ok().body("Friend request denied");
            } else {
                return ResponseEntity.status(400).body("You are already friends with this user");
            }
        }

        Friendship requesterToRequested = friendshipRepository.findByRequesterIdAndReceiverId(clientId, userId);

        // Friendship request exists from requester to requested
        if (requesterToRequested != null) {
            // Friendship request is pending, delete the request
            if (requesterToRequested.getStatus() == Friendship.FriendshipStatus.PENDING) {
                friendshipRepository.delete(requesterToRequested);
                return ResponseEntity.ok().body("Friend request cancelled");
            } else {
                return ResponseEntity.status(400).body("You are already friends with this user");
            }
        }

        // Friendship request does not exist
        return ResponseEntity.status(400).body("You did not send a friend request to this user");
    }

    @PostMapping("/acceptFriendRequest")
    @RequiresJWT
    public ResponseEntity<?> acceptFriendRequest(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        Long requestedId = decodedJWT.getClaim("userId").asLong();

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == requestedId) {
            return ResponseEntity.status(400).body("You cannot accept a friend request from yourself");
        }


        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        if (userId == requestedId) {
            return ResponseEntity.status(400).body("You cannot accept a friend request from yourself");
        }

        User user = optionalUser.get();

        // Check if user is already friends with the user
        Friendship requestedToRequester = friendshipRepository.findByRequesterIdAndReceiverId(userId, requestedId);

        if (requestedToRequester == null) {
            return ResponseEntity.status(400).body("You did not receive a friend request from this user");
        }

        // Friendship request exists from requested to requester, check if it's pending
        if (requestedToRequester.getStatus() == Friendship.FriendshipStatus.PENDING) {
            // Friendship request is pending, accept the request
            requestedToRequester.setStatus(Friendship.FriendshipStatus.FRIENDS);

            friendshipRepository.save(requestedToRequester);

            return ResponseEntity.ok().body("Friend request accepted");
        }

        return ResponseEntity.status(400).body("You are already friends with this user");

    }

    @PostMapping("/unfriendUser")
    @RequiresJWT
    public ResponseEntity<?> unfriendUser(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == decodedJWT.getClaim("userId").asLong()) {
            return ResponseEntity.status(400).body("You cannot unfriend yourself");
        }


        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User user = optionalUser.get();

        // Check if user is already friends with the user
        Friendship requesterToRequested = friendshipRepository.findByRequesterIdAndReceiverId(user.getUserId(), userId);
        Friendship requestedToRequester = friendshipRepository.findByRequesterIdAndReceiverId(userId, user.getUserId());

        // Friendship request exists from requested to requester
        if (requestedToRequester != null) {
            // Friendship request is friended, delete the request
            if (requestedToRequester.getStatus() == Friendship.FriendshipStatus.FRIENDS) {
                friendshipRepository.delete(requestedToRequester);
                return ResponseEntity.ok().body("Unfriended user");
            } else {
                return ResponseEntity.status(400).body("You are not friends with this user");
            }
        }

        // Friendship request exists from requester to requested
        if (requesterToRequested != null) {
            // Friendship request is friended, delete the request
            if (requesterToRequested.getStatus() == Friendship.FriendshipStatus.FRIENDS) {
                friendshipRepository.delete(requesterToRequested);
                return ResponseEntity.ok().body("Unfriended user");
            } else {
                return ResponseEntity.status(400).body("You are not friends with this user");
            }
        }

        // Friendship request does not exist
        return ResponseEntity.status(400).body("You are not friends with this user");
    }

    @PostMapping("/followUser")
    @RequiresJWT
    @Transactional
    public ResponseEntity<?> followUser(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        Long followerId = decodedJWT.getClaim("userId").asLong();

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == followerId) {
            return ResponseEntity.status(400).body("You cannot follow yourself");
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User user = optionalUser.get();

        // Check if user is already following the user
        Following following = followingRepository.findByFollowerIdAndFollowingId(followerId, userId);

        if (following != null) {
            return ResponseEntity.status(400).body("You are already following this user");
        }

        // If not, follow the user
        Following newFollowing = new Following();
        newFollowing.setFollowerId(followerId);
        newFollowing.setFollowingId(userId);

        followingRepository.save(newFollowing);

        return ResponseEntity.ok().body("Followed user");
    }

    @PostMapping("/unfollowUser")
    @RequiresJWT
    public ResponseEntity<?> unfollowUser(@RequestParam("userId") long userId, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");
        Long followerId = decodedJWT.getClaim("userId").asLong();

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        if (userId == followerId) {
            return ResponseEntity.status(400).body("You cannot unfollow yourself");
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User followedUser = optionalUser.get();

        // Check if user is already following the user
        Following following = followingRepository.findByFollowerIdAndFollowingId(followerId, userId);

        if (following == null) {
            return ResponseEntity.status(400).body("You are not following this user");
        }

        // If not, unfollow the user
        followingRepository.delete(following);

        return ResponseEntity.ok().body("Unfollowed user");
    }


}
