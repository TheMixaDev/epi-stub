import React, { useState, useRef, useCallback } from 'react';
import Editor from '@monaco-editor/react';
import SplitPane from 'react-split-pane';
import axios from 'axios';
import './CodeCompiler.css';

interface CompilationResult {
  success: boolean;
  output: string;
  error: string;
  executionTime: number;
  memoryUsed: number;
  exitCode: number;
}

interface Language {
  id: string;
  name: string;
  extension: string;
  monacoLanguage: string;
}

const LANGUAGES: Language[] = [
  { id: 'CPP', name: 'C++', extension: '.cpp', monacoLanguage: 'cpp' },
  { id: 'C', name: 'C', extension: '.c', monacoLanguage: 'c' },
  { id: 'JAVA', name: 'Java', extension: '.java', monacoLanguage: 'java' },
  { id: 'PYTHON', name: 'Python', extension: '.py', monacoLanguage: 'python' },
  { id: 'JAVASCRIPT', name: 'JavaScript', extension: '.js', monacoLanguage: 'javascript' }
];

export const CodeCompiler: React.FC = () => {
  const [code, setCode] = useState<string>('// Write your code here\n');
  const [language, setLanguage] = useState<Language>(LANGUAGES[0]);
  const [optimization, setOptimization] = useState<string>('O2');
  const [result, setResult] = useState<CompilationResult | null>(null);
  const [isCompiling, setIsCompiling] = useState(false);
  const [stdin, setStdin] = useState<string>('');
  const [activeTab, setActiveTab] = useState<'output' | 'errors' | 'info'>('output');
  const editorRef = useRef<any>(null);
  
  const handleEditorDidMount = useCallback((editor: any) => {
    editorRef.current = editor;
    editor.focus();
  }, []);
  
  const handleCompile = async () => {
    setIsCompiling(true);
    setResult(null);
    
    try {
      const response = await axios.post('/api/code/compile-and-run', {
        code,
        language: language.id,
        optimization,
        stdin,
        timeout: 10,
        memoryLimit: 512
      });
      
      setResult(response.data);
      setActiveTab(response.data.success ? 'output' : 'errors');
    } catch (error: any) {
      setResult({
        success: false,
        output: '',
        error: error.response?.data?.message || 'Network error',
        executionTime: 0,
        memoryUsed: 0,
        exitCode: -1
      });
      setActiveTab('errors');
    } finally {
      setIsCompiling(false);
    }
  };
  
  const handleClear = () => {
    setCode('');
    setResult(null);
    setStdin('');
  };
  
  const handleSample = () => {
    const samples: Record<string, string> = {
      CPP: `#include <iostream>
using namespace std;

int main() {
    int n;
    cout << "Enter a number: ";
    cin >> n;
    cout << "Factorial of " << n << " is " << factorial(n) << endl;
    return 0;
}

int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}`,
      C: `#include <stdio.h>

int main() {
    printf("Hello, World!\\n");
    return 0;
}`,
      JAVA: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}`,
      PYTHON: `def factorial(n):
    if n <= 1:
        return 1
    return n * factorial(n - 1)

n = int(input("Enter a number: "))
print(f"Factorial of {n} is {factorial(n)}")`,
      JAVASCRIPT: `function factorial(n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

console.log("Factorial of 5 is", factorial(5));`
    };
    
    setCode(samples[language.id] || '');
  };
  
  return (
    <div className="code-compiler">
      {/* Toolbar */}
      <div className="toolbar">
        <div className="toolbar-left">
          <select 
            value={language.id}
            onChange={(e) => {
              const lang = LANGUAGES.find(l => l.id === e.target.value);
              if (lang) setLanguage(lang);
            }}
            className="language-select"
          >
            {LANGUAGES.map(lang => (
              <option key={lang.id} value={lang.id}>
                {lang.name}
              </option>
            ))}
          </select>
          
          <select 
            value={optimization}
            onChange={(e) => setOptimization(e.target.value)}
            className="optimization-select"
          >
            <option value="O0">No Optimization (-O0)</option>
            <option value="O1">Basic (-O1)</option>
            <option value="O2">Standard (-O2)</option>
            <option value="O3">Aggressive (-O3)</option>
          </select>
        </div>
        
        <div className="toolbar-right">
          <button onClick={handleSample} className="btn btn-secondary">
            Load Sample
          </button>
          <button onClick={handleClear} className="btn btn-secondary">
            Clear
          </button>
          <button 
            onClick={handleCompile} 
            disabled={isCompiling}
            className="btn btn-primary"
          >
            {isCompiling ? (
              <>
                <span className="spinner"></span>
                Compiling...
              </>
            ) : (
              <>
                <span className="icon-run">▶</span>
                Run
              </>
            )}
          </button>
        </div>
      </div>
      
      {/* Split Pane */}
      <SplitPane
        split="vertical"
        minSize={300}
        maxSize={-300}
        defaultSize="50%"
        className="split-pane"
      >
        {/* Left Panel - Code Editor */}
        <div className="editor-panel">
          <div className="panel-header">
            <span className="panel-title">
              Code Editor - {language.name}
            </span>
            <span className="file-info">
              {`main${language.extension}`}
            </span>
          </div>
          
          <Editor
            height="calc(100% - 40px)"
            language={language.monacoLanguage}
            value={code}
            onChange={(value) => setCode(value || '')}
            onMount={handleEditorDidMount}
            theme="vs-dark"
            options={{
              fontSize: 14,
              minimap: { enabled: true },
              scrollBeyondLastLine: false,
              automaticLayout: true,
              lineNumbers: 'on',
              roundedSelection: false,
              renderWhitespace: 'selection',
              cursorStyle: 'line',
              formatOnPaste: true,
              formatOnType: true,
              tabSize: 4,
              wordWrap: 'on'
            }}
          />
        </div>
        
        {/* Right Panel - Compilation Results */}
        <div className="results-panel">
          <div className="panel-header">
            <div className="tabs">
              <button
                className={`tab ${activeTab === 'output' ? 'active' : ''}`}
                onClick={() => setActiveTab('output')}
              >
                Output
                {result?.success && (
                  <span className="badge success">✓</span>
                )}
              </button>
              <button
                className={`tab ${activeTab === 'errors' ? 'active' : ''}`}
                onClick={() => setActiveTab('errors')}
              >
                Errors
                {result && !result.success && (
                  <span className="badge error">✗</span>
                )}
              </button>
              <button
                className={`tab ${activeTab === 'info' ? 'active' : ''}`}
                onClick={() => setActiveTab('info')}
              >
                Info
              </button>
            </div>
          </div>
          
          <div className="results-content">
            {/* Input Section */}
            <div className="stdin-section">
              <label className="stdin-label">Standard Input (stdin):</label>
              <textarea
                value={stdin}
                onChange={(e) => setStdin(e.target.value)}
                placeholder="Enter input for your program..."
                className="stdin-input"
                rows={3}
              />
            </div>
            
            {/* Results Section */}
            {isCompiling ? (
              <div className="loading-state">
                <div className="spinner-large"></div>
                <p>Compiling and executing...</p>
              </div>
            ) : result ? (
              <div className="result-container">
                {/* Output Tab */}
                {activeTab === 'output' && (
                  <div className="output-section">
                    {result.output ? (
                      <pre className="output-text">{result.output}</pre>
                    ) : (
                      <div className="empty-state">
                        No output produced
                      </div>
                    )}
                  </div>
                )}
                
                {/* Errors Tab */}
                {activeTab === 'errors' && (
                  <div className="errors-section">
                    {result.error ? (
                      <pre className="error-text">{result.error}</pre>
                    ) : (
                      <div className="empty-state success">
                        No errors ✓
                      </div>
                    )}
                  </div>
                )}
                
                {/* Info Tab */}
                {activeTab === 'info' && (
                  <div className="info-section">
                    <div className="info-grid">
                      <div className="info-item">
                        <span className="info-label">Status:</span>
                        <span className={`info-value ${result.success ? 'success' : 'error'}`}>
                          {result.success ? 'Success' : 'Failed'}
                        </span>
                      </div>
                      <div className="info-item">
                        <span className="info-label">Exit Code:</span>
                        <span className="info-value">{result.exitCode}</span>
                      </div>
                      <div className="info-item">
                        <span className="info-label">Execution Time:</span>
                        <span className="info-value">
                          {result.executionTime}ms
                        </span>
                      </div>
                      <div className="info-item">
                        <span className="info-label">Memory Used:</span>
                        <span className="info-value">
                          {(result.memoryUsed / 1024 / 1024).toFixed(2)} MB
                        </span>
                      </div>
                      <div className="info-item">
                        <span className="info-label">Language:</span>
                        <span className="info-value">{language.name}</span>
                      </div>
                      <div className="info-item">
                        <span className="info-label">Optimization:</span>
                        <span className="info-value">-{optimization}</span>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="empty-state-large">
                <div className="empty-icon">⚡</div>
                <h3>Ready to compile</h3>
                <p>Click the "Run" button to compile and execute your code</p>
              </div>
            )}
          </div>
        </div>
      </SplitPane>
    </div>
  );
};

export default CodeCompiler;
