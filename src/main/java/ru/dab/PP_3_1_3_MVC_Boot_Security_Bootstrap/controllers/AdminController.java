package ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.models.Role;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.models.User;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.services.RoleService;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.services.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/all-users")
    public String allUsers(Model model,
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
        List<User> users = userService.findAllUsers();
        String username = currentUser.getUsername();
        User mainUser = userService.getByUsername(username);
        Collection<GrantedAuthority> roles = currentUser.getAuthorities();
        model.addAttribute("roles", roles);
        model.addAttribute("username", username);
        model.addAttribute("mainUser", mainUser);
        model.addAttribute("users", users);
        User newUser = new User();
        Set<Role> allRoles = roleService.findAllRoles();
        model.addAttribute("newUser", newUser);
        model.addAttribute("allRoles", allRoles);
        return "/admin/all-users";
    }


    @GetMapping("/edituserbyid")
    public String editUserView(@RequestParam("id") Long id, Model model) {
        User editUser = userService.getById(id);
        Set<Role> allRoles = roleService.findAllRoles();
        model.addAttribute("user", editUser);
        model.addAttribute("allRoles", allRoles);
        return "admin/edit-user";
    }

    @PostMapping("/userupdate")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.update(user);
        return "redirect:/admin/all-users";
    }

    @GetMapping("/deleteuserbyid")
    public String deleteUserAccount(@RequestParam("id") Long id) {
        User user = userService.getById(id);
        user.setRoles(new HashSet<>());
        userService.delete(id);
        return "redirect:/admin/all-users";
    }
}
