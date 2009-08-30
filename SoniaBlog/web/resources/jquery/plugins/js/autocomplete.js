/* *
 * Copyright (c) 2009, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of JAB; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://kenai.com/projects/jab
 *
 */


(function($){

  $.fn.extend({
    autocomplete: function( url, options ){
      this.each( function(){
        new $.Autocomplete(this, url, options);
      });
    }

  });

  $.Autocomplete = function( field, url, options )
  {
    var defaults = {
      id: 'jqueryAutocomplete',
      maxItems : 5,
      minChars : 2
    };

    options = $.extend({},defaults, options);

    var $field = $(field);
    var $output = $("#" + options.id);
    var selected = null;

    var index = -1;

    $field.blur(function(){
      setTimeout( function(){
        $output.hide()
      }, 200);
    });

    $field.keydown(function(event){
      switch ( event.keyCode )
      {
        // ESC
        case 27:
          $output.hide();
          break;
        // UP
        case 38:
          selectUp();
          break;
        // DOWN
        case 40:
          selectDown();
          break;
        // RETURN
        case 13:
          processSelection();
          break;
        // LEFT
        case 37:
           // do nothing
           break;
        // Right
        case 39:
           // do nothing
           break;
        // ALL OTHER
        default:
          processKeyPress();
      }
    });

    function selectUp()
    {
      if ( index > 0 )
      {
              index--;
      select();
      }
    }

    function selectDown()
    {
      index++;
      select();
    }

    function select()
    {
      var $children = $output.children();
      $children.removeClass( "selected" );
      if ( $children.length >= index )
      {
        var $child = $children.eq(index);
        $child.addClass("selected");
        selected = $child.children().eq(0).attr("href");
      }
      else
      {
        index--;
      }
    }

    function processSelection()
    {
      if ( selected != null )
      {
        location.href = selected;
      }
    }

    function processKeyPress()
    {
      var value = $field.val();
      clear();
      if ( value.length >= options.minChars )
      {
        value += "~";
        if ($output.length == "")
        {
          createOutputContainer();
        }
        $output.show();

        url = createQueryUrl(value);

        $.getJSON( url, function(result)
        {
          $.each(result, function(index, content)
          {
            if ( index < options.maxItems )
            {
              $output.append( createOutputElement(content) );
            }

          });
        });
      }
    }

    function clear()
    {
      index = -1;
      selected = null;
      $output.empty();
    }

    function createOutputElement(content)
    {
      return $("<li />").append($("<a />").attr("href", content.url).text(content.value) );
    }

    function createQueryUrl(value)
    {
      var index = url.indexOf( "?query=" );
      if ( index > 0 )
      {
        url = url.substring(0, index);
      }
      url += "?query=" + value;
      return url;
    }

    function createOutputContainer()
    {
      // TODO replace the 4
      var p = findPosition(field, $field.height() +4 );
      $("body").append( $("<ul />").attr("id", options.id).addClass( "autocomplete" ).css( "top",  p.top + "px").css( "left", p.left + "px" ) );
      $output = $("#" + options.id);
      $output.width( $field.width() );
    }

    function findPosition( el, height )
    {
      var top = height;
      var left = 0;
      do {
        left += el.offsetLeft;
        top += el.offsetTop;
        el = el.offsetParent;
      } while(el.tagName.toLowerCase() != 'body');

      return {
        top: top,
        left: left
      }
    }

  }

})(jQuery);