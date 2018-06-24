package modules;

import com.google.inject.AbstractModule;
import interfaces.repositories.IAttributeTranslationRepository;
import interfaces.repositories.ICodeTypeRepository;
import interfaces.repositories.ILanguageDefinitionRepository;
import interfaces.repositories.ITranslatableAttributeRepository;
import play.libs.akka.AkkaGuiceSupport;
import repos.EbeanAttributeTranslationRepository;
import repos.EbeanCodeTypeRepository;
import repos.EbeanLanguageDefinitionRepository;
import repos.EbeanTranslatableAttributeRepository;

public class TranslationRepositoriesModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bind(ILanguageDefinitionRepository.class).
                to(EbeanLanguageDefinitionRepository.class);
        bind(ITranslatableAttributeRepository.class).
                to(EbeanTranslatableAttributeRepository.class);
        bind(ICodeTypeRepository.class).
                to(EbeanCodeTypeRepository.class);
        bind(IAttributeTranslationRepository.class).
                to(EbeanAttributeTranslationRepository.class);
    }
}
