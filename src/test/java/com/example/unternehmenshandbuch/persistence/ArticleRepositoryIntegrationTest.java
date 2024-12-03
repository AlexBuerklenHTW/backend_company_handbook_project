package com.example.unternehmenshandbuch.persistence;

import com.example.unternehmenshandbuch.model.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ArticleRepositoryIntegrationTest {

	@Autowired
	private ArticleRepository articleRepository;

	private Article article1;

	@BeforeEach
	public void setup() {
		articleRepository.deleteAll();

		article1 = Article.builder()
				.title("Title1")
				.description("Description1")
				.content("Content1")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("User1")
				.build();

		Article article2 = Article.builder()
				.title("Title2")
				.description("Description2")
				.content("Content2")
				.version(1)
				.status(Article.ArticleStatus.APPROVED)
				.editedBy("User2")
				.build();

		article1 = articleRepository.save(article1);
		articleRepository.save(article2);

		Article article1Version2 = Article.builder()
				.title("Title1 - Version 2")
				.description("Description1 - Version 2")
				.content("Content1 - Version 2")
				.version(2)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("User1")
				.build();

		articleRepository.save(article1Version2);
	}

	@Test
	public void testSaveArticle() {
		Article article = Article.builder()
				.title("New Title")
				.description("New Description")
				.content("New Content")
				.version(1)
				.status(Article.ArticleStatus.EDITING)
				.editedBy("User3")
				.build();

		Article savedArticle = articleRepository.save(article);

		assertThat(savedArticle.getId()).isNotNull();
		assertThat(savedArticle.getTitle()).isEqualTo("New Title");
	}

	@Test
	public void testFindById() {
		Optional<Article> fetchedArticle = articleRepository.findById(article1.getId());

		assertThat(fetchedArticle).isPresent();
		assertThat(fetchedArticle.get().getId()).isEqualTo(article1.getId());
	}

//	@Test
//	public void testFindFirstByPublicId() {
//		String publicId = article1.getPublicId();
//
//		Optional<Article> fetchedArticle = articleRepository.findFirstByPublicId(publicId);
//
//		assertThat(fetchedArticle).isPresent();
//		assertThat(fetchedArticle.get().getPublicId()).isEqualTo(publicId);
//	}

	@Test
	public void testFindAll() {
		Iterable<Article> articles = articleRepository.findAll();
		assertThat(articles).hasSize(3);  // 2 main articles + 1 version
	}

	@Test
	public void testDeleteById() {
		articleRepository.deleteById(article1.getId());

		Optional<Article> deletedArticle = articleRepository.findById(article1.getId());
		assertThat(deletedArticle).isNotPresent();
	}

	@Test
	public void testDeleteAll() {
		articleRepository.deleteAll();
		Iterable<Article> articles = articleRepository.findAll();
		assertThat(articles).isEmpty();
	}

	@Test
	public void testUpdateArticle() {
		article1.setTitle("Updated Title");
		Article updatedArticle = articleRepository.save(article1);

		assertThat(updatedArticle.getTitle()).isEqualTo("Updated Title");
	}
}
