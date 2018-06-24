package modules;

import com.google.inject.AbstractModule;
import interfaces.repositories.IMarkdownMediaReferenceRepository;
import interfaces.repositories.IMediaObjectRepository;
import interfaces.repositories.IMediaReferenceRepository;
import play.libs.akka.AkkaGuiceSupport;
import repos.*;


public class MediaRepositoryModule extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bind(IMediaReferenceRepository.class).
                to(EbeanMediaReferenceRepository.class);
        bind(IMediaObjectRepository.class).
                to(EbeanMediaObjectRepository.class);
        bind(IMarkdownMediaReferenceRepository.class).
                to(EbeanMarkdownMediaReferenceRepository.class);
    }
}