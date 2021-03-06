package cn.eurasia.oj.controllers;

import cn.eurasia.oj.annotations.Auth;
import cn.eurasia.oj.entities.Quiz;
import cn.eurasia.oj.entities.User;
import cn.eurasia.oj.exceptions.BusinessException;
import cn.eurasia.oj.requestParams.CreateQuizParam;
import cn.eurasia.oj.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/quizzes")
public class QuizController {

  @Autowired
  private QuizService quizService;

  @GetMapping("pageable")
  public ResponseEntity getQuizzesByPage(
    @PageableDefault(sort = {"id"},
      direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(quizService.getQuizzesByPage(pageable));
  }

  @GetMapping("wrong/pageable")
  public ResponseEntity getWrongQuizzesByPage(
    @Auth User user,
    @PageableDefault(sort = {"id"},
      direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(quizService.getWrongQuizzesByPage(pageable, user));
  }

  @GetMapping("")
  public ResponseEntity getQuizzes() {
    return ResponseEntity.ok(quizService.getQuizzes());
  }

  @PostMapping("")
  public ResponseEntity addQuiz(@RequestBody CreateQuizParam quizParam,
                                       @Auth User current) {

    Quiz quiz = quizService.addQuiz(quizParam,current);
    return ResponseEntity.created(URI.create("/api/quizzes/"+quiz.getId())).build();
  }

  @PutMapping("")
  public ResponseEntity editQuiz(@RequestBody CreateQuizParam quizParam) throws BusinessException {
    quizService.editQuiz(quizParam);
    return ResponseEntity.noContent().build();
  }

}
