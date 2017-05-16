package controllers.forms;

public class CreateQuizForm {

    private String questionSource;
    private Boolean singleAnswer;
    private String questionType;
    private Integer questionCount;

    public CreateQuizForm() {}

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getQuestionSource() {
        return questionSource;
    }

    public void setQuestionSource(String questionSource) {
        this.questionSource = questionSource;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Boolean getSingleAnswer() {
        return singleAnswer;
    }

    public void setSingleAnswer(Boolean singleAnswer) {
        this.singleAnswer = singleAnswer;
    }
}