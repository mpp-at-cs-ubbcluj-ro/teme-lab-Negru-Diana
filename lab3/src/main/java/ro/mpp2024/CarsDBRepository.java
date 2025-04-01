package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository{

    private JdbcUtils dbUtils;



    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry("Finding cars by manufacturer: {}", manufacturerN);
        Connection con = dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();

        if (con == null) {
            logger.error("Database connection is null.");
            return cars;
        }

        try (PreparedStatement preparedStatement = con.prepareStatement(
                "SELECT * FROM Masini WHERE manufacturer = ?")) {
            preparedStatement.setString(1, manufacturerN);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String manufacturer = resultSet.getString("manufacturer");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException ex) {
            logger.error("Error fetching cars: {}", ex.getMessage());
        }

        return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
	//to do
        return null;
    }

    @Override
    public void add(Car elem) {
        logger.traceEntry("saving task {}", elem);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStm = con.prepareStatement("insert into Masini (manufacturer, model, year) values (?,?,?)")){
            preStm.setString(1, elem.getManufacturer());
            preStm.setString(2, elem.getModel());
            preStm.setInt(3, elem.getYear());
            int result = preStm.executeUpdate();
            logger.trace("Saved {} instances", result);
        }
        catch(SQLException e){
            logger.error(e);
            System.err.println(("Error DB " + e));
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Car elem) {
      //to do
    }

    @Override
    public Iterable<Car> findAll() {
         logger.traceEntry();
         Connection con = dbUtils.getConnection();
         List<Car> cars = new ArrayList<>();
         try(PreparedStatement preStm = con.prepareStatement("select * from Masini")){
             try(ResultSet result=preStm.executeQuery()){
                 while(result.next()){
                     int id=result.getInt("id");
                     String manufacturer = result.getString("manufacturer");
                     String model = result.getString("model");
                     int year = result.getInt("year");
                     Car car = new Car(manufacturer, model, year);
                     car.setId(id);
                     cars.add(car);
                 }
             }
         } catch (SQLException e) {
             logger.error(e);
             System.err.println(("Error DB " + e));
         }
         logger.traceExit();
         return cars;
    }
}
