package org.optimizationBenchmarking.utils.tools.impl.process;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.parallel.ByteProducerConsumerBuffer;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJob;

/**
 * An external process with which you can communicate via standard streams
 * that cannot deadlock.
 */
public final class ExternalProcess extends ToolJob implements Closeable {

  /** the wrapped process instance */
  Process m_process;

  /** a stream providing the stdout of the process */
  InputStream m_stdout;
  /**
   * the worker thread associated with stdout, or {@code null} if none is
   * needed
   */
  _WorkerThread m_stdoutWorker;
  /**
   * the byte buffer associated with stdout, or {@code null} if none is
   * needed
   */
  ByteProducerConsumerBuffer m_stdoutBuffer;

  /** a stream providing stderr of the process */
  InputStream m_stderr;
  /**
   * the worker thread associated with stderr, or {@code null} if none is
   * needed
   */
  _WorkerThread m_stderrWorker;
  /**
   * the byte buffer associated with stderr, or {@code null} if none is
   * needed
   */
  ByteProducerConsumerBuffer m_stderrBuffer;

  /** a stream providing stdint of the process */
  OutputStream m_stdin;
  /**
   * the worker thread associated with stdin, or {@code null} if none is
   * needed
   */
  _WorkerThread m_stdinWorker;
  /**
   * the byte buffer associated with stdin, or {@code null} if none is
   * needed
   */
  ByteProducerConsumerBuffer m_stdinBuffer;

  /** the process' name */
  private final String m_name;

  /** an error caught somewhere */
  private Object m_error;

  /**
   * create
   * 
   * @param process
   *          the process
   * @param log
   *          the logger to use
   * @param name
   *          the process' name
   */
  ExternalProcess(final Process process, final Logger log,
      final String name) {
    super(log);
    this.m_process = process;
    this.m_name = name;
  }

  /** start all threads associated with this process */
  final void _start() {
    final Logger logger;
    __UncaughtExceptionHandler ueh;

    ueh = null;

    if (this.m_stdinWorker != null) {
      ueh = new __UncaughtExceptionHandler();
      this.m_stdinWorker.setUncaughtExceptionHandler(ueh);
      this.m_stdinWorker.start();
    }

    if ((this.m_stdoutWorker != null)
        && (this.m_stdoutWorker != this.m_stdinWorker)) {
      if (ueh == null) {
        ueh = new __UncaughtExceptionHandler();
      }
      this.m_stdoutWorker.setUncaughtExceptionHandler(ueh);
      this.m_stdoutWorker.start();
    }

    if ((this.m_stderrWorker != null)
        && (this.m_stderrWorker != this.m_stdinWorker)
        && (this.m_stderrWorker != this.m_stdoutWorker)) {
      if (ueh == null) {
        ueh = new __UncaughtExceptionHandler();
      }
      this.m_stderrWorker.setUncaughtExceptionHandler(ueh);
      this.m_stderrWorker.start();
    }

    logger = this.getLogger();
    if ((logger != null) && (logger.isLoggable(Level.FINE))) {
      logger.fine("Successfully started: " + this.m_name); //$NON-NLS-1$
    }
  }

  /**
   * Wait until the process has finished and obtain its return value.
   * 
   * @return the return value
   */
  public final int waitFor() {
    try {
      return this.__close(false);
    } catch (final IOException ioe) {
      return (-1);// we should never get here
    }
  }

  /**
   * Terminate the process if it is still alive
   * 
   * @param kill
   *          should we use force?
   * @return the process' return value
   * @throws IOException
   *           if i/o fails
   */
  private final int __close(final boolean kill) throws IOException {
    final Logger logger;
    Object error;
    boolean shouldMessage, shouldKill;
    int returnValue;

    logger = this.getLogger();
    error = null;
    shouldMessage = false;
    returnValue = (-1);
    // <kill the main process>
    if (this.m_process != null) {
      try {
        shouldKill = kill;
        if (!kill) {
          waiter: for (;;) {
            try {
              returnValue = this.m_process.waitFor();
              break waiter;
            } catch (final InterruptedException ie) {
              // ingore
            } catch (final Throwable tt) {
              shouldKill = true;
              error = ErrorUtils.aggregateError(tt, error);
              ErrorUtils
                  .logError(
                      logger,//
                      ("Error while calling 'waitFor' during shutting down process " + this.m_name), //$NON-NLS-1$
                      tt, false);
              break waiter;
            }
          }
        }

        if (shouldKill) {
          this.m_process.destroy();
          shouldMessage = true;
        }
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils.logError(logger,//
            ("Error while shutting down process " + this.m_name), //$NON-NLS-1$
            t, false);
      } finally {
        this.m_process = null;
      }
    }
    // </kill the main process>

    // <kill stdout>
    if (this.m_stdout != null) {
      try {
        this.m_stdout.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdout during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdout = null;
      }
    }

    if (this.m_stdoutBuffer != null) {
      try {
        this.m_stdoutBuffer.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdout buffer during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdoutBuffer = null;
      }
    }

    if (this.m_stdoutWorker != null) {
      try {
        shouldKill = kill;
        if (!kill) {
          waiter: for (;;) {
            try {
              this.m_stdoutWorker.m_mode = 1;
              this.m_stdoutWorker.join();
              break waiter;
            } catch (final InterruptedException ie) {
              // ingore
            } catch (final Throwable tt) {
              shouldKill = true;
              error = ErrorUtils.aggregateError(tt, error);
              ErrorUtils
                  .logError(
                      logger,//
                      ("Error while waiting for stdout worker shutting down process " + this.m_name), //$NON-NLS-1$
                      tt, false);
              break waiter;
            }
          }
        }

        if (shouldKill) {
          this.m_stdoutWorker.m_mode = 2;
        }
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdout worker during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdoutWorker = null;
      }
    }
    // </kill stdout>

    // <kill stderr>
    if (this.m_stderr != null) {
      try {
        this.m_stderr.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stderr during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stderr = null;
      }
    }

    if (this.m_stderrBuffer != null) {
      try {
        this.m_stderrBuffer.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stderr buffer during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stderrBuffer = null;
      }
    }

    if (this.m_stderrWorker != null) {
      try {
        shouldKill = kill;
        if (!kill) {
          waiter: for (;;) {
            try {
              this.m_stderrWorker.m_mode = 1;
              this.m_stderrWorker.join();
              break waiter;
            } catch (final InterruptedException ie) {
              // ingore
            } catch (final Throwable tt) {
              shouldKill = true;
              error = ErrorUtils.aggregateError(tt, error);
              ErrorUtils
                  .logError(
                      logger,//
                      ("Error while waiting for stderr worker during shutting down process " + this.m_name), //$NON-NLS-1$
                      tt, false);
              break waiter;
            }
          }
        }

        if (shouldKill) {
          this.m_stderrWorker.m_mode = 2;
        }
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stderr worker during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stderrWorker = null;
      }
    }
    // </kill stderr>

    // <kill stdin>
    if (this.m_stdin != null) {
      try {
        this.m_stdin.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdin during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdin = null;
      }
    }

    if (this.m_stdinBuffer != null) {
      try {
        this.m_stdinBuffer.close();
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdin buffer during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdinBuffer = null;
      }
    }

    if (this.m_stdinWorker != null) {
      try {
        shouldKill = kill;
        if (!kill) {
          waiter: for (;;) {
            try {
              this.m_stdinWorker.m_mode = 1;
              this.m_stdinWorker.join();
              break waiter;
            } catch (final InterruptedException ie) {
              // ingore
            } catch (final Throwable tt) {
              shouldKill = true;
              error = ErrorUtils.aggregateError(tt, error);
              ErrorUtils
                  .logError(
                      logger,//
                      ("Error while waiting for stdin worker during shutting down process " + this.m_name), //$NON-NLS-1$
                      tt, false);
              break waiter;
            }
          }
        }

        if (shouldKill) {
          this.m_stdinWorker.m_mode = 2;
        }
      } catch (final Throwable t) {
        error = ErrorUtils.aggregateError(t, error);
        ErrorUtils
            .logError(
                logger,//
                ("Error while closing stdin worker during shutting down process " + this.m_name), //$NON-NLS-1$
                t, false);
      } finally {
        this.m_stdinWorker = null;
      }
    }

    // </kill stdin>

    error = ErrorUtils.aggregateError(error, this.m_error);
    if (error != null) {
      if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {

        logger.log(Level.SEVERE,
            ((kill ? "Error while forcefully killing " : //$NON-NLS-1$
                "Error while gracefully shutting down ")//$NON-NLS-1$
            + this.m_name),//
            ((error instanceof Throwable) ? ((Throwable) error) : null));
      }
      if (kill) {
        ErrorUtils.throwIOException((//
            "Error while forcefully killing external process "//$NON-NLS-1$
            + this.m_name), error);
      } else {
        this.m_error = error;
      }
    } else {
      if (kill) {
        if (shouldMessage) {
          if ((logger != null) && (logger.isLoggable(Level.WARNING))) {
            logger.warning("Forcefully killed " + this.m_name); //$NON-NLS-1$
          }
        }
      } else {
        if ((logger != null) && (logger.isLoggable(Level.FINE))) {
          logger.fine("Gracefully shut down " + this.m_name + //$NON-NLS-1$
              ", return value=" + returnValue);//$NON-NLS-1$
        }
      }
    }

    return returnValue;
  }

  /**
   * Terminate the process if it is still alive
   * 
   * @throws IOException
   *           if i/o fails
   */
  @Override
  public final void close() throws IOException {
    this.__close(true);
  }

  /**
   * Get the standard-output stream of the process
   * 
   * @return the standard-output stream of the process
   */
  public final InputStream getStdOut() {
    return this.m_stdout;
  }

  /**
   * Get the standard-error stream of the process
   * 
   * @return the standard-error stream of the process
   */
  public final InputStream getStdError() {
    return this.m_stderr;
  }

  /**
   * Get the standard-input stream of the process
   * 
   * @return the standard-input stream of the process
   */
  public final OutputStream getStdIn() {
    return this.m_stdin;
  }

  /**
   * Add an error
   * 
   * @param t
   *          the error
   */
  synchronized final void _addError(final Throwable t) {
    if (t != null) {
      this.m_error = ErrorUtils.aggregateError(t, this.m_error);
    }
  }

  /** the internal exception handler for worker threads */
  private final class __UncaughtExceptionHandler implements
      UncaughtExceptionHandler {

    /** create */
    __UncaughtExceptionHandler() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    public final void uncaughtException(final Thread t, final Throwable e) {
      ExternalProcess.this._addError(e);
    }
  }

}
