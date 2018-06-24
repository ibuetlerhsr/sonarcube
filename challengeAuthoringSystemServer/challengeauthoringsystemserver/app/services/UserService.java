package services;

import interfaces.repositories.*;
import models.DatabaseObject.user.MandantCCS;
import models.DatabaseObject.user.Role;
import models.DatabaseObject.user.UserCAS;
import models.DatabaseObject.user.UserMandantRole;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class UserService {

    private final String defaultLanguageId = "en";
    private Map<String, String> languageMap;

    private final IUserMandantRoleRepository userMandantRoleRepository;
    private final IUserCASRepository userCASRepository;
    private final IRoleRepository roleRepository;
    private final IMessageRepository messageRepository;
    private final IMandantCCSRepository mandantCCSRepository;


    @Inject
    private UserService(IUserMandantRoleRepository userMandantRoleRepository,
                        IUserCASRepository userCASRepository,
                        IRoleRepository roleRepository,
                        IMessageRepository messageRepository,
                        IMandantCCSRepository mandantCCSRepository) {
        this.userMandantRoleRepository = userMandantRoleRepository;
        this.userCASRepository = userCASRepository;
        this.roleRepository = roleRepository;
        this.messageRepository = messageRepository;
        this.mandantCCSRepository = mandantCCSRepository;
    }

    public void intializeUserDbValues(){
        if (this.roleRepository.find().query().findCount() == 0) {
            Role newRoleR = new Role();
            newRoleR.setRoleName("Reviewer");
            this.roleRepository.insert(newRoleR);

            Role newRoleC = new Role();
            newRoleC.setRoleName("Author");
            this.roleRepository.insert(newRoleC);

            Role newRoleCo = new Role();
            newRoleCo.setRoleName("Contributor");
            this.roleRepository.insert(newRoleCo);

            Role newRoleT = new Role();
            newRoleT.setRoleName("Translator");
            this.roleRepository.insert(newRoleT);

            Role newRoleU = new Role();
            newRoleU.setRoleName("User");
            this.roleRepository.insert(newRoleU);

            Role newRoleA = new Role();
            newRoleA.setRoleName("Admin");
            this.roleRepository.insert(newRoleA);


        }
    }

    public Long createUser(UserCAS userCAS){

        CompletionStage<Long> id =this.userCASRepository.insert(userCAS);
        CompletableFuture<Long> future = id.toCompletableFuture();
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    public void processUserMandantRole(Long userID, Map<MandantCCS, Set<Role>> mandantCCSSetMap){
        if(userID == 0 || mandantCCSSetMap == null)
            return;
        for(MandantCCS mandantCCS : mandantCCSSetMap.keySet()){
            MandantCCS oldMandant = this.mandantCCSRepository.find().query().where().eq("name", mandantCCS.getMandantName()).findUnique();
            if(oldMandant == null){
                CompletionStage<Long> id = this.mandantCCSRepository.insert(mandantCCS);
                CompletableFuture<Long> future = id.toCompletableFuture();
                try {
                    createRoles(future.get(), userID, mandantCCSSetMap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                createRoles(oldMandant.getId(), userID, mandantCCSSetMap);
            }
        }
    }

    private void createRoles(Long mandantId, Long userID, Map<MandantCCS, Set<Role>> mandantCCSSetMap ){
        MandantCCS mandantCCS = this.mandantCCSRepository.find().query().where().eq("id", mandantId).findUnique();
        Set<Role> roles = null;
        for(MandantCCS mandantCCSKey : mandantCCSSetMap.keySet()) {
            if(mandantCCSKey.getMandantName().equals(mandantCCS.getMandantName())) {
                roles = mandantCCSSetMap.get(mandantCCSKey);
            }
        }
        for (Role role : roles){
            UserMandantRole userMandantRole = new UserMandantRole();
            userMandantRole.setUser(this.userCASRepository.find().query().where().eq("id", userID).findUnique());
            Role newRole = this.roleRepository.find().query().where().eq("roleName", role.getRoleName()).findUnique();
            if(newRole != null){
                userMandantRole.setRole(newRole);
            } else {
                CompletionStage<Long> roleId = this.roleRepository.insert(role);
                CompletableFuture<Long> future1 = roleId.toCompletableFuture();
                try {
                    userMandantRole.setRole(this.roleRepository.find().query().where().eq("id", future1.get()).findUnique());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            userMandantRole.setMandant(this.mandantCCSRepository.find().query().where().eq("id", mandantId).findUnique());
            this.userMandantRoleRepository.insert(userMandantRole);
        }
    }

    public UserCAS findByAuthToken(String token){
        return this.userCASRepository.find().query().where().eq("authToken", token).findUnique();
    }

    public UserCAS findByUsernameAndPassword(String userName, String password){
        return this.userCASRepository.find().query().where().eq("username", userName).eq("password", password).findUnique();
    }
}
