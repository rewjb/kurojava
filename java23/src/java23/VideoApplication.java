package java23;

import  java.awt.*;
import  java.awt.event.*;
import  java.io.*;
import  java.net.*;
import  javax.swing.*;
import  javax.media.*;

public  class  VideoApplication  extends  JFrame  {
        private  Player  player;  //  �ڹ�  �̵��  �����

        private  Component  visualMedia;  //  �ð���  ����  ������Ʈ

        private  Component  mediaControl;  //  �̵�  ����  ������Ʈ  ����

        private  Container  container;  //  ��  ��ü�̳�

        private  File  mediaFile;  //  �̵��  ���ϰ�  �̵��  ��ġ

        private  URL  fileURL;

        public  VideoApplication()  {  //  ������
                super("Video  Application  player");
                container  =  getContentPane();
                JMenu  fileMenu  =  new  JMenu("File");  //  �޴�  ����
                fileMenu.setMnemonic('F');
                container.add(fileMenu,  BorderLayout.NORTH);
                //  �޴���  Open  File  Ŭ��
                JMenuItem  openItem  =  new  JMenuItem("Open  File");  //  ����  ����
                openItem.setMnemonic('O');
                openItem.addActionListener(new  ActionListener()  {
                        public  void  actionPerformed(ActionEvent  event)  {
                                mediaFile  =  getFile();  //  getFile  �޼ҵ�  ����
                                if  (mediaFile  !=  null)  {
                                        try  {
                                                fileURL  =  mediaFile.toURL();
                                        }  catch  (MalformedURLException  badURL)  {
                                                badURL.printStackTrace();
                                                showErrorMessage("Bad  URL");
                                        }
                                        makePlayer(fileURL.toString());
                                }
                        }
                });
                //  ����  ����

                fileMenu.add(openItem);
                JMenuItem  openURLItem  =  new  JMenuItem("Open  Locator");  //  URL  �̿�  ���
                openURLItem.setMnemonic('L');
                openURLItem.addActionListener(new  ActionListener()  {
                        public  void  actionPerformed(ActionEvent  event)  {
                                String  addressName  =  getMediaLocation();
                                if  (addressName  !=  null)  {
                                        makePlayer(addressName);
                                }
                        }
                });
                fileMenu.add(openURLItem);
                JMenuItem  exitItem  =  new  JMenuItem("Exit");  //  ����
                exitItem.setMnemonic('x');
                exitItem.addActionListener(new  ActionListener()  {
                        public  void  actionPerformed(ActionEvent  event)  {
                                System.exit(0);
                        }
                });
                fileMenu.add(exitItem);
                JMenuBar  bar  =  new  JMenuBar();
                setJMenuBar(bar);
                bar.add(fileMenu);
                Manager.setHint(Manager.LIGHTWEIGHT_RENDERER,  Boolean.TRUE);
        }

        public  void  showErrorMessage(String  error)  {  //  �˾�  ����  �޽���
                JOptionPane.showMessageDialog(this,  error,  "Error",
                                JOptionPane.ERROR_MESSAGE);
        }

        //  ����ڰ�  ����  ��ǻ�Ϳ���  �̵��  Ŭ����  �����ϰ�  �Ѵ�
        public  File  getFile()  {  //  ��ǻ�ͷκ���  ������  ����
                JFileChooser  fileChooser  =  new  JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int  result  =  fileChooser.showOpenDialog(this);
                if  (result  ==  JFileChooser.CANCEL_OPTION)  {
                        return  null;
                }  else  {
                        return  fileChooser.getSelectedFile();
                }
        }

        public  String  getMediaLocation()  {  //  �����  �Է�����  ����  ��ġ
                String  input  =  JOptionPane.showInputDialog(this,  "Enter  URL");
                if  (input  !=  null  &&  input.length()  ==  0)  {
                        return  null;
                }
                return  input;
        }

        //  Ŭ����  Player��  �����ϱ�  ����  �ʿ���  �غ�  �۾���  ����
        public  void  makePlayer(String  mediaLocation)  {  //  ��ġ��  ����  �÷��̾�  ����
                if  (player  !=  null)  {
                        removePlayerComponents();
                }
                MediaLocator  mediaLocator  =  new  MediaLocator(mediaLocation);
                if  (mediaLocator  ==  null)  {
                        showErrorMessage("Error  opening  file");
                        return;
                }
                try  {
                        player  =  Manager.createPlayer(mediaLocator);
                        //  createPlayer()  ��  ȣ�������ν���ο�  Player�޼ҵ带  �ʱ�ȭ  �Ѵ�.
                        //  createPlayer()  ��  ������  �̵��  �ҽ���  ����  �׿�  ������  Player��  �����Ѵ�.
                        player.addControllerListener(new  PlayerEventHandler());
                        player.realize();
                }  //  ���Ṯ����  IOException
                catch  (NoPlayerException  noPlayerException)  {
                        noPlayerException.printStackTrace();
                }  catch  (IOException  ioException)  {
                        ioException.printStackTrace();
                }
        }

        //  ���ο�  Player��  �����ϱ�  ����  ����  Player��  �ð�  ������Ʈ��  GUI  ��Ʈ����  �����ӿ���  �����Ѵ�
        public  void  removePlayerComponents()  {  //  �÷��̾�  �ڿ�  ��ȯ,  �̵��/��Ʈ��  �ʱ�ȭ
                if  (visualMedia  !=  null)  {
                        container.remove(visualMedia);
                }
                if  (mediaControl  !=  null)  {
                        container.remove(mediaControl);
                }
                //  ���  �÷��̾���  Ȱ��  ����,  ������  Player����  ���ϰ�  �ִ�  �ý���  �ڿ���  ��ȯ�Ѵ�.
                player.close();
        }

        public  void  getMediaComponents()  {  //  �ð�  �̵���  �÷��̾�  ��Ʈ��  ����
                visualMedia  =  player.getVisualComponent();
                if  (visualMedia  !=  null)  {
                        container.add(visualMedia,  BorderLayout.CENTER);
                }
                mediaControl  =  player.getControlPanelComponent();
                if  (mediaControl  !=  null)  {
                        container.add(mediaControl,  BorderLayout.SOUTH);
                }
        }

        private  class  PlayerEventHandler  extends  ControllerAdapter  {  //  �ڵ鷯
                public  void  realizeComplete(RealizeCompleteEvent  realizeDoneEvent)  {
                        player.prefetch();
                }

                public  void  prefetchComplete(  //  pretetching  ��  ����۾�  ����
                                PrefetchCompleteEvent  prefetchDoneEvent)  {
                        getMediaComponents();
                        validate();
                        player.start();
                }

                public  void  endOfMedia(EndOfMediaEvent  mediaEndEvent)  {
                        player.setMediaTime(new  Time(0));
                        player.stop();
                }
        }

        public  static  void  main(String  args[])  {
                VideoApplication  testPlayer  =  new  VideoApplication();
                testPlayer.setSize(400,  400);
                testPlayer.setLocation(400,  400);
                testPlayer.setDefaultCloseOperation(EXIT_ON_CLOSE);
                testPlayer.setVisible(true);
        }
}

