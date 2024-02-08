package com.mirakl.hybris.core.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ReflectionUtils;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;

public class MiraklServiceLayerTest extends ServicelayerTest {

  // Backport of @AppendSpringConfiguration annotation for older Hybris versions
  @Before
  @Override
  public void prepareApplicationContextAndSession() throws Exception {
    if (this.getClass().isAnnotationPresent(MiraklAppendSpringConfiguration.class)) {
      String[] xmlFiles = this.getClass().getAnnotation(MiraklAppendSpringConfiguration.class).value();
      final ApplicationContext parentContext = Registry.getApplicationContext();
      ApplicationContext applicationContext = new ClassPathXmlApplicationContext(xmlFiles, parentContext);
      autowireFields(applicationContext, this);
    } else {
      super.prepareApplicationContextAndSession();
    }
  }

  private void autowireFields(ApplicationContext applicationContext, final Object test) {
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    final Set<String> missing = new LinkedHashSet<>();

    ReflectionUtils.doWithFields(test.getClass(), new ReflectionUtils.FieldCallback() {

      public void doWith(Field field) throws IllegalArgumentException {
        Resource resource = field.getAnnotation(Resource.class);

        if (resource != null) {
          field.setAccessible(true);
          Object bean = ReflectionUtils.getField(field, test);

          if (bean == null) {
            String beanName = findBeanName(resource, field);
            bean = beanFactory.getBean(beanName, field.getType());
            if (bean != null) {
              ReflectionUtils.setField(field, test, bean);
            } else {
              missing.add(field.getName());
            }
          }
        }
      }

    });

    if (!missing.isEmpty()) {
      String testClassName = test.getClass().getSimpleName();
      String msg = "test " + testClassName + " is not properly initialized - missing bean references " + missing;
      throw new IllegalStateException(msg);
    }
  }

  private String findBeanName(Resource resource, Field field) {
    if (resource.mappedName() != null && resource.mappedName().length() > 0) {
      return resource.mappedName();
    } else {
      return resource.name() != null && resource.name().length() > 0 ? resource.name() : field.getName();
    }
  }

}
