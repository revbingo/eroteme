package modules;

import com.google.inject.AbstractModule;
import models.BuzzerQuestionAsker;
import models.QuestionAsker;

public class ErotemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(QuestionAsker.class).to(BuzzerQuestionAsker.class);
    }
}
