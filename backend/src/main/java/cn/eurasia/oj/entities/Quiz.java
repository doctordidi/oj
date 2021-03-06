package cn.eurasia.oj.entities;

import cn.eurasia.oj.requestParams.CreateQuizParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "quiz")
@NoArgsConstructor
public class Quiz {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String description;
  private String options;
  private Integer answer;
  private String chapter;
  private String level;
  @ManyToOne
  @JoinColumn(name = "majorId")
  private Major major;
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date createTime;

  public Quiz(String description, String options, Integer answer, String chapter, Major major, User current, String level) {
    this.description = description;
    this.options = options;
    this.answer = answer;
    this.chapter = chapter;
    this.major = major;
    this.user = current;
    this.level = level;
  }

  public static Quiz convertParam(CreateQuizParam quizParam, User current) {
    return new Quiz(quizParam.getDescription(), quizParam.getOptions(),
      quizParam.getAnswer(), quizParam.getChapter(),
      new Major(quizParam.getMajor()), current, quizParam.getLevel());
  }

  public void update(CreateQuizParam quizParam, Major major) {
    this.description = quizParam.getDescription();
    this.options = quizParam.getOptions();
    this.answer = quizParam.getAnswer();
    this.chapter = quizParam.getChapter();
    this.major = major;
    this.level = quizParam.getLevel();
  }
}