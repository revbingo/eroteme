package modules;

import com.google.inject.AbstractModule;
import models.FreeQuestionAsker;
import models.QuestionAsker;

public class ErotemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(QuestionAsker.class).to(FreeQuestionAsker.class);
    }
}
