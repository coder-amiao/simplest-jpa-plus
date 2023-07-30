//package cn.soboys.simplestjpa;
//
//import cn.soboys.simplestjpa.test.entity.Category;
//import cn.soboys.simplestjpa.test.entity.QCategory;
//import cn.soboys.simplestjpa.test.service.ICategoryService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@SpringBootTest
//@Slf4j
//class SimplestJpaApplicationTests {
//    @Autowired
//    private ICategoryService categoryService;
//
//    @Test
//    void query() {
//        QCategory qCategory = QCategory.category;
//        List<Category> categoryList = categoryService.queryChain().selectFrom(qCategory).fetch();
//        log.info("数据{}",categoryList.size());
//    }
//
//    @Test
//    @Rollback(value = false)
//    @Transactional
//    void update(){
//        QCategory qCategory = QCategory.category;
//        categoryService.updateChain(qCategory)
//                .set(qCategory.title,"6666")
//                .execute();
//
//    }
//
//    @Test
//    @Rollback(value = false)
//    void del(){
//       categoryService.removeById(8l);
//    }
//
//    @Test
//    @Rollback(value = false)
//    void save(){
//        Category category=new Category();
//        category.setTitle("ces");
//       categoryService.save(category);
//
//    }
//
//
//
//}
