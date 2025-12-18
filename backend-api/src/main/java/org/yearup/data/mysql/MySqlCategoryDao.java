package org.yearup.data.mysql;

import com.mysql.cj.protocol.Resultset;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {

        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        // get all categories
        List<Category> categories = new ArrayList<>();
        String sql = """
                SELECT * FROM categories
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                categories.add(mapRow(resultSet));
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("OH NO SOMETHING WENT WRONG");
        }
    }

    @Override
    public Category getById(int categoryId) {
        // get category by id
        String sql = """
                SELECT * FROM categories WHERE category_id = ?;
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, categoryId);

            ResultSet result = preparedStatement.executeQuery();
            result.next();
            return mapRow(result);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("OH NO WE HAVE A PROBLEM");
        }
    }

    @Override
    public Category create(Category category) {
        // create a new category
        String sql = """
                INSERT INTO categories (name, description) VALUE (?,?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                category.setCategoryId(resultSet.getInt(1));
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("WE GOT A PROBLEM");
        }
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category
        String sql = """
                UPDATE categories SET name = ?, description ? WHERE category_id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setString(3, String.valueOf(categoryId));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("OH NO SOMETHING WENT WRONG");
        }


    }

    @Override
    public void delete(int categoryId) {
        // delete category
        String sql = """
                DELETE FROM categories WHERE category_id = ?
                """;
        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
           preparedStatement.setInt(1, categoryId);
           preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("WE HAVE A PROBLEM");
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
