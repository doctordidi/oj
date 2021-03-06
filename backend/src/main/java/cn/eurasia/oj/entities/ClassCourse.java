package cn.eurasia.oj.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "classCourse")
public class ClassCourse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String code;
  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date createTime;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date endTime;
  @ManyToMany
  @JoinTable(name = "classCoursePaper",joinColumns = @JoinColumn(name = "classCourseId"),
    inverseJoinColumns = @JoinColumn(name = "paperId"))
  private List<Paper> papers;
  @ManyToMany
  @JoinTable(name = "userClassCourse",joinColumns = @JoinColumn(name = "classCourseId"),
    inverseJoinColumns = @JoinColumn(name = "userId"))
  private List<User> users;

  public void update(ClassCourse classCourse) {
    this.title = classCourse.title;
    this.code = classCourse.code;
    this.endTime = classCourse.endTime;
    this.papers = classCourse.getPapers();
  }
}