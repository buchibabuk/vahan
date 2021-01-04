package dao;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class DataSourceConnect {
    
  private static ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    
   public  static Object getConnect(String rtoCode)
    {
    int intRtoCode = Integer.parseInt(rtoCode);   
    Object dao=null;   
    
      switch(intRtoCode)
      {
          case 0:
          dao=(Object)context.getBean("dao"+rtoCode);
          break;
              
          case 1:    
          dao=(Object)context.getBean("dao"+rtoCode);
          break;
      }
   
    return dao;
    }
    
}
