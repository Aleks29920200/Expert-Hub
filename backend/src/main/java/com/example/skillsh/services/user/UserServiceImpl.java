package com.example.skillsh.services.user;

import com.example.skillsh.domain.dto.file.FileUploadModel;
import com.example.skillsh.domain.dto.user.*;
import com.example.skillsh.domain.entity.*;
import com.example.skillsh.domain.entity.enums.OrderStatus;
import com.example.skillsh.domain.entity.enums.Status;
import com.example.skillsh.domain.view.UserView;
import com.example.skillsh.repository.RoleRepo;
import com.example.skillsh.repository.ServiceOrderRepository;
import com.example.skillsh.repository.SkillRepo;
import com.example.skillsh.repository.UserRepo;
import com.example.skillsh.services.comment.CommentService;
import com.example.skillsh.services.file.FileService;
import com.example.skillsh.services.review.ReviewService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final FileService fileService;
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private ModelMapper mapper=new ModelMapper();
    private RoleRepo roleRepo;
    private SkillRepo skillRepo;
    private FileService service;
    private ReviewService reviewService;
    private CommentService commentService;
    private final ServiceOrderRepository orderRepository;
    List<Skill> skillList=new ArrayList<>();
    @Autowired
    public UserServiceImpl(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, RoleRepo roleRepo, SkillRepo skillRepo, DispatcherServletRegistrationBean dispatcherServletRegistrationBean, FileService service, DelegatingFilterProxyRegistrationBean securityFilterChainRegistration, FileService fileService, ReviewService reviewService, CommentService commentService, ServiceOrderRepository orderRepository) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
        this.roleRepo = roleRepo;
        this.skillRepo = skillRepo;
        this.service = service;
        this.fileService = fileService;
        this.reviewService = reviewService;
        this.commentService = commentService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void registerUserAsExpert(RegisterAsExpertDto registerAsExpertDto) {
        User user=new User();
        user.setEmail(registerAsExpertDto.getEmail());
        user.setFirstName(registerAsExpertDto.getFirstName());
        user.setLastName(registerAsExpertDto.getLastName());
        user.setUsername(registerAsExpertDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerAsExpertDto.getPassword()));
        user.setActivity(Status.OFFLINE);
        FileUploadModel file=new FileUploadModel(registerAsExpertDto.getPhotoUrl());
        try {
            service.upload(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.setPhotoUrl(service.getCurrentAddedImage());
        if(userRepo.count()==0){

            Set<Role> roles=new HashSet<>();
            roles.add(roleRepo.getById(1L));
            user.setRole(roles);
            userRepo.save(user);
        }else{
            Set<Role> roles=new HashSet<>();
            roles.add(roleRepo.getById(2L));
            user.setRole(roles);
            userRepo.save(user);
            Skill skillByCategory = this.skillRepo.getSkillByCategory(registerAsExpertDto.getCategoryOfSkill());
            skillList.add(skillByCategory);
            user.setSkills(skillList);
        }
        userRepo.save(user);
    }
    @Override
    public void registerUserAsClient(RegisterAsClientDto registerAsExpertDto) {
        User user=new User();
        user.setEmail(registerAsExpertDto.getEmail());
        user.setFirstName(registerAsExpertDto.getFirstName());
        user.setLastName(registerAsExpertDto.getLastName());
        user.setUsername(registerAsExpertDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerAsExpertDto.getPassword()));
        user.setActivity(Status.OFFLINE);
        FileUploadModel file=new FileUploadModel(registerAsExpertDto.getPhotoUrl());
        try {
            service.upload(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.setPhotoUrl(service.getCurrentAddedImage());
        if(userRepo.count()==0){
            Set<Role> roles=new HashSet<>();
            roles.add(roleRepo.getById(1L));
            user.setRole(roles);
            userRepo.save(user);
        }else{
            Set<Role> roles=new HashSet<>();
            roles.add(roleRepo.getById(3L));
            user.setRole(roles);
            userRepo.save(user);
            user.setSkills(skillList);
        }
        userRepo.save(user);
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return this.userRepo.findById(aLong);
    }

    @Override
    public List<User> searchUsersByInfo(String keyword) {
        return userRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                keyword, keyword, keyword
        );
    }
    public List<UserView> searchUsers(String keyword) {
        // 1. Взимаме списъка с намерени експерти (както си го правил досега)
        List<User> experts = userRepo.searchExpertsByKeyword(keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            experts = userRepo.findAll(); // или userRepo.findAllExperts(), зависи как се казва методът ти
        }
        // АКО ИМА ВЪВЕДЕНА ДУМА -> Търсим по нея
        else {
            experts = userRepo.searchExpertsByKeyword(keyword);
        }
        // 2. Взимаме кой е текущо логнатият потребител
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            currentUsername = auth.getName(); // Връща username-а на логнатия
        }

        final String finalCurrentUsername = currentUsername;

        // 3. Мапваме Entity към DTO и проверяваме за плащане
        return experts.stream().map(expert -> {
            UserView view = mapper.map(expert, UserView.class);

            // Ако потребителят е логнат, проверяваме дали е платил на този експерт
            if (finalCurrentUsername != null) {
                boolean paid = orderRepository.existsByBuyer_UsernameAndOffer_Seller_IdAndStatus(finalCurrentUsername, expert.getId(), OrderStatus.PAID.toString());
                view.setHasPaid(paid);
            } else {
                // Ако е гост (нелогнат), няма как да е платил
                view.setHasPaid(false);
            }

            return view;
        }).collect(Collectors.toList());
    }

    @Override
    public User setUserStatus(User user) {
        user.setActivity(Status.ONLINE);
        return userRepo.save(user);
    }

    @Override
    public void updateStatus(String username, Status status) {
        User u = userRepo.findUserByUsername(username).orElse(null);
        if (u != null) {
            u.setActivity(status);
            userRepo.save(u);
        }
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAllByActivity(Status.ONLINE);
    }


    @Override
    @Transactional
    public UserProfileDTO getUserByUsername(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO dto = mapper.map(user, UserProfileDTO.class);

        // Manually set the photo if the auto-mapper failed
        if (user.getPhotoUrl() != null) {
            dto.setPhotoUrl(Arrays.toString(user.getPhotoUrl().getFileData())); // Use the actual base64 field
        }

        return dto;
    }


    @Override
    public Optional<User> findUserByUsername(String username) {
        return this.userRepo.findUserByUsername(username);
    }
    public UserView findUser(String username) {
        return mapper.map(this.userRepo.findUserByUsername(username),UserView.class);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return this.userRepo.findUserByEmail(email);
    }

    @Override
    public List<UserView> users(){
        return this.userRepo.findAll().stream().map(e->mapper.map(e,UserView.class)).toList();
    }

    @Override
    public List<User> searchByFirstName(String firstName) {
        return this.userRepo.searchByFirstName(firstName);
    }

    @Override
    public List<UserView> findAllBySkills(List<Long> skillids) {
        return this.userRepo.findAllBySkills_IdIn(skillids).stream().
                map(e->mapper.map(e,UserView.class)).toList();
    }


    @Override
    public void registerO2AuthUser(OAuth2User oauth2User) {
        User user=new User();
        Set<Role> roles=new HashSet<>();
        if(userRepo.count()==0){
            roles.add(roleRepo.getById(1L));
        }
        roles.add(roleRepo.getById(2L));
        user.setRole(roles);
        user.setUsername(oauth2User.getAttribute("username"));
        user.setEmail(oauth2User.getAttribute("email"));
        user.setActivity(Status.OFFLINE);
        user.getSkills().add(this.skillRepo.getById(1L));
        Object picture = oauth2User.getAttribute("picture");
        String imageUrl = null;

        if (picture instanceof Map) {
            Map<String, Object> pictureMap = (Map<String, Object>) picture;
            Object data = pictureMap.get("data");

            if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                imageUrl = (String) dataMap.get("url");

                if (imageUrl != null) {
                    RestTemplate restTemplate = new RestTemplate();
                    byte[] imageData = restTemplate.getForObject(imageUrl, byte[].class);


                    FileEntity profilePicture = new FileEntity();
                    profilePicture.setFileName("facebook_profile_picture.jpg");
                    profilePicture.setContentType("image/jpeg");
                    profilePicture.setFileData(imageData);

                    user.setPhotoUrl(profilePicture);
                }
            }
        }
        userRepo.save(user);
    }

    @Override
    public void addUser(@Valid AddUserDTO addUserDto) {
        this.registerUserAsExpert(mapper.map(addUserDto, RegisterAsExpertDto.class));
    }
    // 1. READ - Вземане на всички потребители
    public List<UserDTO> findAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 2. CREATE - Записване на нов потребител
    public UserDTO saveUser(UserDTO userDTO) {
        User user = new User();

        // 1. Старите полета
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // 2. НОВИТЕ ПОЛЕТА (които добавихме във фронтенда)
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAddress(userDTO.getAddress());
        user.setPicture(userDTO.getPicture());

        // 3. Обработка на паролата (вече идва от Angular формата)
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            // Защитна мрежа: ако някой хакер прати заявка без парола
            user.setPassword(passwordEncoder.encode("123456"));
        }

        // 4. Задаване на роля (с поправения Set.of код отпреди малко)
        List<Role> userRoles = roleRepo.getRoleByName("USER");

// Конвертираш ArrayList-а в HashSet
        Set<Role> roles = new HashSet<>(userRoles);

// Задаваш сета на потребителя
        user.setRole(roles);


        // 5. Записваме в базата
        User savedUser = userRepo.save(user);

        return mapToDTO(savedUser); // Увери се, че и mapToDTO прехвърля новите полета!
    }
    // 3. UPDATE - Редактиране на съществуващ потребител
    @Transactional
    public UserDTO updateUser2(Long id, UserDTO userDTO) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Потребителят не е намерен"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // Можеш да добавиш логика за смяна на роля тук
        // if (userDTO.getRole() != null) { ... }

        User updatedUser = userRepo.save(user);
        return mapToDTO(updatedUser);
    }

    // 4. DELETE - Изтриване


    // Помощен метод за превръщане на Entity в DTO
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        if (user.getRole() != null) {
            Set<Role> roleNames = user.getRole();
            dto.setRoles((List<Role>) roleNames);
        }
        return dto;
    }
    @Override
    public void updateUser(AddUserDTO user) {
       User byId = this.userRepo.findById(user.getId()).orElse(null);
       if(user.getEmail()!=null){
           byId.setEmail(user.getEmail());
       }
       if(user.getFirstName()!=null){
           byId.setFirstName(user.getFirstName());
       }
        if(user.getUsername()!=null){
            byId.setUsername(user.getUsername());
        }
        if(user.getPassword()!=null){
            byId.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if(user.getPhotoUrl()!=null) {
            FileUploadModel file = new FileUploadModel((MultipartFile) user.getPhotoUrl());
            try {
                service.upload(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byId.setPhotoUrl(service.getCurrentAddedImage());
        }
        byId.setPhotoUrl(service.getCurrentAddedImage());
        userRepo.save(byId);
    }

    @Override
    public void deleteUser(Long id) {
        this.userRepo.deleteById(id);
    }

    @Transactional
    public void blockUser(String blockerUsername, String blockedUsername) {
        Optional<User> blockerOpt = userRepo.findUserByUsername(blockerUsername);
        Optional<User> blockedOpt = userRepo.findUserByUsername(blockedUsername);

        if (blockerOpt.isEmpty()) {
            throw new IllegalArgumentException("Blocker user '" + blockerUsername + "' not found.");
        }
        if (blockedOpt.isEmpty()) {
            throw new IllegalArgumentException("Blocked user '" + blockedUsername + "' not found.");
        }

        User blocker = blockerOpt.get();
        User blocked = blockedOpt.get();


        if (blocker.getBlockedUsers() == null) {
            blocker.setBlockedUsers(new ArrayList<>());
        }

        if (blocker.getBlockedUsers().contains(blocked)) {
            throw new IllegalArgumentException("User '" + blockedUsername + "' is already blocked by '" + blockerUsername + "'.");
        }

        blocker.getBlockedUsers().add(blocked);
        userRepo.save(blocker);
    }




    @Transactional
    public void unblockUser(String blockerUsername, String unblockedUsername) {
        User blocker = userRepo.findUserByUsername(blockerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Unblocker user '" + blockerUsername + "' not found."));
        User unblocked = userRepo.findUserByUsername(unblockedUsername)
                .orElseThrow(() -> new IllegalArgumentException("Unblocked user '" + unblockedUsername + "' not found."));

        // Check if actually blocked before trying to unblock
        if (!blocker.getBlockedUsers().contains(unblocked)) {
            throw new IllegalArgumentException("User '" + unblockedUsername + "' is not currently blocked by '" + blockerUsername + "'.");
        }

        blocker.getBlockedUsers().remove(unblocked);
        userRepo.save(blocker);
    }

    @Transactional
    public Map<String, List<String>> getBlockStatus(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElse(null);

        if (user == null) {
            return Map.of("blocked", Collections.emptyList(), "blockedBy", Collections.emptyList());
        }


        List<String> blocked = user.getBlockedUsers().stream()
                .map(User::getUsername)
                .toList();

        List<String> blockedBy = userRepo.findUsersWhoBlocked(userRepo.findById(user.getId()).get()).stream()
                .map(User::getUsername)
                .toList();

        return Map.of("blocked", blocked, "blockedBy", blockedBy);
    }
    @Override
    public List<User> getUsersWithReviews() {
        return this.userRepo.findAll().stream().filter(e -> e.getRole().stream().noneMatch(f -> Objects.equals(f.getName(), "ADMIN")))
                .map(user -> {
                    List<Review> reviews = reviewService.findReviewByReviewedUser(user);
                    reviews.forEach(review -> review.setComments(commentService.findCommentByReview(review)));
                    user.setReview(reviews);
                    return user;
                }).collect(Collectors.toList());
    }
    public List<User>mapUsers(){
        return this.userRepo.findAll();
    }

}
