/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.blog.office;

//~--- non-JDK imports --------------------------------------------------------

import sonia.blog.api.app.BlogContext;
import sonia.blog.api.app.BlogRequest;
import sonia.blog.api.app.Constants;
import sonia.blog.api.dao.AttachmentDAO;
import sonia.blog.api.dao.Dao;
import sonia.blog.api.macro.AbstractBlogMacro;
import sonia.blog.api.macro.LinkResource;
import sonia.blog.api.macro.ScriptResource;
import sonia.blog.api.macro.WebMacro;
import sonia.blog.api.macro.WebResource;
import sonia.blog.entity.Attachment;
import sonia.blog.entity.Blog;
import sonia.blog.entity.ContentObject;
import sonia.blog.entity.Entry;

import sonia.config.Config;

//~--- JDK imports ------------------------------------------------------------

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 *
 * @author sdorra
 */
public class PdfViewerMacro extends AbstractBlogMacro implements WebMacro
{

  /** Field description */
  public static final String NAME = "pdfviewer";

  /** Field description */
  private static String PDFMIMETYPE = "application/pdf";

  /** Field description */
  private static Logger logger =
    Logger.getLogger(PdfViewerMacro.class.getName());

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public List<WebResource> getResources()
  {
    return resources;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * Method description
   *
   *
   * @param theme
   */
  public void setTheme(String theme)
  {
    this.theme = theme;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param request
   * @param linkBase
   * @param object
   * @param body
   *
   * @return
   */
  @Override
  protected String doBody(BlogRequest request, String linkBase,
                          ContentObject object, String body)
  {
    String result = null;
    Blog blog = request.getCurrentBlog();
    BlogContext context = BlogContext.getInstance();

    if ((object != null) && (object instanceof Entry))
    {
      if (id != null)
      {
        Attachment attachment = attachmentDAO.findByBlogAndId(blog, id);

        if (attachment.getMimeType().equalsIgnoreCase(PDFMIMETYPE))
        {
          File attachmentFile =
            context.getResourceManager().getFile(attachment);

          if (attachmentFile.exists())
          {
            result = createPdfImageGallery(request, linkBase, id,
                                           attachmentFile, body);
          }
          else
          {
            result = "-- file not found --";
          }
        }
        else
        {
          result = "-- file is not a pdf --";
        }
      }
      else
      {
        result = "-- id param is required --";
      }
    }
    else
    {
      result = "-- entry not found --";
    }

    return result;
  }

  /**
   * Field description
   *
   * @param request
   * @param linkBase
   * @param id
   * @param attachmentFile
   * @param body
   *
   * @return
   */

  /**
   * Method description
   *
   *
   *
   * @param request
   * @param linkBase
   * @param id
   * @param attachmentFile
   * @param body
   *
   * @return
   */
  private String createPdfImageGallery(BlogRequest request, String linkBase,
          long id, File attachmentFile, String body)
  {
    String result = null;
    File resDir = BlogContext.getInstance().getResourceManager().getDirectory(
                      Constants.RESOURCE_ATTACHMENT, request.getCurrentBlog());
    File pdfDir = new File(resDir, "pdfviewer" + File.separator + id);

    if (pdfDir.exists())
    {
      result = printPdfImageGallery(linkBase, pdfDir, body);
    }
    else
    {
      if (pdfDir.mkdirs())
      {
        try
        {
          createPdfImageGallery(pdfDir, attachmentFile);
          result = printPdfImageGallery(linkBase, pdfDir, body);
        }
        catch (Exception ex)
        {
          logger.log(Level.SEVERE, null, ex);
          result = "-- " + ex.getLocalizedMessage() + " --";
        }
      }
      else
      {
        result = "-- cant create pdf directory --";
      }
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param pdfDir
   * @param attachmentFile
   *
   * @throws IOException
   */
  private void createPdfImageGallery(File pdfDir, File attachmentFile)
          throws IOException
  {
    RandomAccessFile raf = new RandomAccessFile(attachmentFile, "r");
    FileChannel channel = raf.getChannel();
    ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                                 channel.size());
    PDFFile pdf = new PDFFile(buf);
    int pages = pdf.getNumPages();

    for (int i = 1; i < pages; i++)
    {
      PDFPage page = pdf.getPage(i);

      createPdfPage(page,
                    new File(pdfDir, String.valueOf(i) + "." + extension));
    }
  }

  /**
   * Method description
   *
   *
   * @param page
   * @param file
   *
   * @throws IOException
   */
  private void createPdfPage(PDFPage page, File file) throws IOException
  {
    Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(),
                                   (int) page.getBBox().getHeight());
    BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height,
                                    BufferedImage.TYPE_INT_RGB);

    // generate the image
    Image image = page.getImage(rect.width, rect.height,    // width & height
                                rect,                       // clip rect
                                null,                       // null for the ImageObserver
                                true,                       // fill background with white
                                true                        // block until drawing is done
                                  );
    Graphics2D bufImageGraphics = bufferedImage.createGraphics();

    bufImageGraphics.drawImage(image, 0, 0, null);
    ImageIO.write(bufferedImage, format, file);
  }

  /**
   * Method description
   *
   *
   *
   * @param linkBase
   * @param pdfDir
   * @param body
   *
   * @return
   */
  private String printPdfImageGallery(String linkBase, File pdfDir, String body)
  {
    StringBuffer result = new StringBuffer();
    String res = linkBase + "resources/prettyPhoto/";

    resources = new ArrayList<WebResource>();

    ScriptResource jquery = new ScriptResource(20,
                              linkBase + "resources/jquery/jquery.min.js");

    resources.add(jquery);

    LinkResource css = new LinkResource(100);

    css.setRel(LinkResource.REL_STYLESHEET);
    css.setType(LinkResource.TYPE_STYLESHEET);
    css.setHref(res + "css/prettyPhoto.css");
    resources.add(css);

    ScriptResource js = new ScriptResource(101,
                          res + "js/jquery.prettyPhoto.js");

    resources.add(js);

    File[] files = pdfDir.listFiles(new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        return name.endsWith("." + extension);
      }
    });

    long time = System.nanoTime();
    String name = "pdfgallery_" + pdfDir.getName();

    result.append("<span id=\"").append(name).append("\">\n");

    String path = pdfDir.getName() + "/";
    String baseLink = linkBase + "macros/pdfviewer/" + path;

    if ((files != null) && (files.length > 0))
    {
      for (int i = 1; i < files.length; i++)
      {
        String link = baseLink + i + "." + extension;

        result.append("<a title=\"Page ").append(i).append("\" ");
        result.append("rel=\"prettyPhoto[").append(time);
        result.append("]\" href=\"").append(link).append("\"");

        if (i > 1)
        {
          result.append(" style=\"display: none;\">");
        }
        else
        {
          if ((body == null) || (body.trim().length() == 0))
          {
            StringBuffer bodyBuffer = new StringBuffer();

            bodyBuffer.append("<img src=\"").append(linkBase);
            bodyBuffer.append("resource/icon/document.gif\" alt=\"PDF\" />");
            body = bodyBuffer.toString();
          }

          result.append(">").append(body);
        }

        result.append("</a>\n");
      }
    }

    result.append("</span>\n");
    result.append("<script type=\"text/javascript\">\n");
    result.append("$(document).ready(function() {\n");
    result.append("$(\"span#").append(name).append(" a\").prettyPhoto({\n");
    result.append( "theme: '" ).append(theme).append("'\n");
    result.append("});\n");
    result.append("});\n");
    result.append("</script>\n");

    return result.toString();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Dao
  private AttachmentDAO attachmentDAO;

  /** Field description */
  @Config(Constants.CONFIG_IMAGEEXTENSION)
  private String extension = Constants.DEFAULT_IMAGE_EXTENSION;

  /** Field description */
  @Config(Constants.CONFIG_IMAGEFORMAT)
  private String format = Constants.DEFAULT_IMAGE_FORMAT;

  /** Field description */
  private Long id;

  /** Field description */
  private List<WebResource> resources;

  /** Field description */
  private String theme = "dark_square";
}
