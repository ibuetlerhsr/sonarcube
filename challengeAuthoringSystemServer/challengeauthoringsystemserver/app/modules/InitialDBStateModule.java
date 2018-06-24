package modules;

import com.google.inject.AbstractModule;
import models.DatabaseObject.InitialDBState;
import play.libs.akka.AkkaGuiceSupport;

public class InitialDBStateModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure(){
        bind(InitialDBState.class).asEagerSingleton();
    }
}
