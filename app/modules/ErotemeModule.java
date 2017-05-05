package modules;

import com.google.inject.AbstractModule;
import models.FixedQuestionAsker;
import models.QuestionAsker;

public class ErotemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(QuestionAsker.class).to(FixedQuestionAsker.class);
    }
}
