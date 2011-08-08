package com.t_arn.JavaIDEdroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import com.t_arn.lib.io.StringWriterOutputStream;

//##################################################################
public class BeanShellTask 
  extends AsyncTask<String, String, Void> // android.os.AsyncTask<Params, Progress, Result>
//##################################################################
{ 
  private ProgressDialog progressDialog;
  private StringWriterOutputStream swos;
  
public BeanShellTask ()
//===================================================================
{
} //
//===================================================================
protected void onPreExecute() 
//===================================================================
{
  Log.i(G.stProgramName, "onPreExecute");
  // show progress dialog
  progressDialog = new ProgressDialog(G.main);
  progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
  progressDialog.setTitle("BeanShell Task");
  progressDialog.setMessage("Task running...");
  progressDialog.setIndeterminate(true);
  progressDialog.show();
}
//===================================================================
protected Void doInBackground(String... args) 
//===================================================================
{
  try
  {
    final bsh.Interpreter i;
    Log.i(G.stProgramName, "doInBackground");
    i = new bsh.Interpreter();
    i.set("G", new G());
    swos = new StringWriterOutputStream();
    G.ide.fnRedirectOutput(swos);
    G.ide.fnRunBeanshellScript(i, args[0]);
  }
  catch (Throwable t)
  {
  G.fnError("doInBackground", t);
  }
  return null;
}
//===================================================================
protected void onProgressUpdate(String... progress)
{
  String[] uitask;
  try
  {
    Log.i(G.stProgramName, "onProgressUpdate");
    if (!progress[0].startsWith("~UITASK~")) progressDialog.setMessage(progress[0]);
    else
    {
      uitask = progress[0].split("\t");
      if (uitask[1].equals("CLEAR")) 
      {
        swos.toStringBuffer().setLength(0);
        G.main.fnClear();
      }
      if (uitask[1].equals("TOAST"))
      { 
        G.fnToast(uitask[2], Integer.parseInt(uitask[3]));
      }
    }//else
  } catch (Exception e) { Log.e(G.stProgramName, "onProgressUpdate: "+e.getMessage()); }
} // onProgressUpdate
//===================================================================
protected void onPostExecute(Void unused) 
//===================================================================
{ 
  publishProgress("Task finished");
  G.tabBeanshell_tvOutput.append(swos.toString());
  if (progressDialog.isShowing()) progressDialog.dismiss();
} 
//===================================================================
public void fnClear()
{
  publishProgress("~UITASK~\tCLEAR");
}
//===================================================================
public void fnPublishProgress(String progressMessage)
{
  publishProgress(progressMessage);
}
//===================================================================
public void fnToast(String msg, int ms)
{
  publishProgress("~UITASK~\tTOAST\t"+msg+"\t"+ms);
}
//===================================================================
}
//##################################################################
