package modules;

import com.google.inject.AbstractModule;

import interfaces.repositories.*;
import play.libs.akka.AkkaGuiceSupport;
import repos.*;


public class UserRepositoriesModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bind(IUserMandantRoleRepository.class).
                to(EbeanUserMandantRoleRepository.class);
        bind(IUserCASRepository.class).
                to(EbeanUserCASRepository.class);
        bind(IRoleRepository.class).
                to(EbeanRoleRepository.class);
        bind(IMessageRepository.class).
                to(EbeanMessageRepository.class);
        bind(IMandantCCSRepository.class).
                to(EbeanMandantCCSRepository.class);
    }
}
