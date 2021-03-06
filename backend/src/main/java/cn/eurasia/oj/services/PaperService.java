package cn.eurasia.oj.services;

import cn.eurasia.oj.entities.*;
import cn.eurasia.oj.exceptions.BusinessException;
import cn.eurasia.oj.repositories.PaperRepository;
import cn.eurasia.oj.repositories.QuizSubmissionRepository;
import cn.eurasia.oj.repositories.ReviewQuizRepository;
import cn.eurasia.oj.requestParams.CreatePaperParam;
import cn.eurasia.oj.requestParams.CreatePaperSubmissionParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PaperService {
  @Autowired
  private PaperRepository paperRepository;
  @Autowired
  private QuizSubmissionRepository quizSubmissionRepository;
  @Autowired
  private ReviewQuizRepository reviewQuizRepository;

  public Paper addPaper(CreatePaperParam createPaperParam, User current) {
    Paper paper = Paper.convertParam(createPaperParam, current);
    return paperRepository.save(paper);
  }

  public Page<Paper> getQuizzesByPage(Pageable pageable) {
    return paperRepository.findAll(pageable);
  }

  public void editPaper(Paper paper) throws BusinessException {
    Paper currentPaper = paperRepository.findById(paper.getId()).orElseThrow(
      () -> new BusinessException("未找到该试卷")
    );
    currentPaper.update(paper);
    paperRepository.save(paper);
  }

  public List<Paper> findAll() {
    return paperRepository.findAll();
  }

  public Paper findPaper(Long paperId) throws BusinessException {
    Paper paper = paperRepository.findById(paperId).orElseThrow(
      () -> new BusinessException("未找到")
    );
    paper.removeQuizzesAnswer();
    return paper;
  }

  public void submitPaper(Long classCourseId, Long paperId, CreatePaperSubmissionParam createPaperSubmissionParam, Long userId) throws BusinessException {
    ReviewQuiz reviewQuiz = reviewQuizRepository.findByClassCourseIdAndPaperIdAndUserId(classCourseId, paperId, userId);
    if (Objects.nonNull(reviewQuiz)) {
      throw new BusinessException("已提交答案");
    }

    Map<String, Long> submission = createPaperSubmissionParam.getSubmission();
    Paper paper = paperRepository.findById(paperId).orElseThrow(() -> new BusinessException("不存在"));

    dealSubmission(classCourseId, submission, paper, userId);
    dealReviewQuiz(classCourseId, submission, paper, userId);
  }

  private void dealSubmission(Long classCourseId, Map<String, Long> submission, Paper paper, Long userId) {
    List<QuizSubmission> quizSubmissions = submission.keySet().stream().map(quizId -> {

      Long count = paper.getQuizzes().stream().filter(quiz -> quiz.getId().equals(Long.valueOf(quizId))
      && quiz.getAnswer().equals(Integer.valueOf(submission.get(quizId).toString())))
        .count();
      boolean isCorrect = count > 0 ;
      return new QuizSubmission(classCourseId, paper.getId(), Long.valueOf(quizId),Integer.valueOf(submission.get(quizId).toString()),isCorrect , userId);
    }).collect(Collectors.toList());

    quizSubmissionRepository.saveAll(quizSubmissions);
  }

  private void dealReviewQuiz(Long classCourseId, Map<String, Long> submission, Paper paper, Long userId) {
    Double score = calculateScore(submission, paper);
    ReviewQuiz reviewQuiz = new ReviewQuiz(classCourseId, paper.getId(), userId, score);
    reviewQuizRepository.save(reviewQuiz);
  }

  private Double calculateScore(Map<String, Long> submission, Paper paper) {
    List<Quiz> quizzes = paper.getQuizzes();
    long correctCount = quizzes.stream().filter(quiz ->
      quiz.getAnswer().toString().equals(submission.get(quiz.getId().toString()).toString()))
      .count();
    return Double.valueOf(new DecimalFormat("#.00").format(correctCount * 1.0 / quizzes.size() * 100));
  }

  public Map getPaperReviewQuiz(Long classCourseId, Long paperId, Long userId) throws BusinessException {
    Map result = new HashMap();
    Paper paper = paperRepository.findById(paperId).orElseThrow(
      () -> new BusinessException("未找到")
    );
    List<Long> quizIds = paper.getQuizzes().stream().map(Quiz::getId).collect(Collectors.toList());
    List<QuizSubmission> submission = quizSubmissionRepository.findByClassCourseIdAndPaperIdAndUserIdAndQuizIdIn(classCourseId, paper.getId(), userId, quizIds);
    ReviewQuiz reviewQuiz = reviewQuizRepository.findByClassCourseIdAndPaperIdAndUserId(classCourseId, paperId, userId);
    result.put("paper", paper);
    result.put("submission", submission);
    result.put("reviewQuiz", reviewQuiz);
    return result;
  }
}
