package com.mirakl.hybris.channels.filters;

import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MiraklOCCChannelFilter extends OncePerRequestFilter {

  protected MiraklChannelService miraklChannelService;
  protected MiraklChannelResolvingStrategy miraklChannelResolvingStrategy;

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
      final FilterChain filterChain) throws ServletException, IOException {
    miraklChannelService.setCurrentMiraklChannel(miraklChannelResolvingStrategy.resolveCurrentChannel());

    filterChain.doFilter(request, response);
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

  @Required
  public void setMiraklChannelResolvingStrategy(MiraklChannelResolvingStrategy miraklChannelResolvingStrategy) {
    this.miraklChannelResolvingStrategy = miraklChannelResolvingStrategy;
  }
}
