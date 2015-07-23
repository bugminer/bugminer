'use strict';

module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({

    mvnSrcDirectory: '../../',
    appPath: '<%= mvnSrcDirectory %>main/resources/public/',
    targetPath: '../../../target/classes/public/',
    javascriptPath: '<%= appPath %>js/',
    cssPath: '<%= appPath %>css/',
    fontPath: '<%= appPath %>fonts/',
    imgPath: '<%= appPath %>img/',
    partialPath: '<%= appPath %>partials/',
    templatePath: '<%= appPath %>templates/',

    pkg: grunt.file.readJSON('package.json'),
    bowerrc: grunt.file.readJSON('.bowerrc'),

    clean: {
      options: {
        force: true
      },
      app: ['<%= javascriptPath %>', '<%= cssPath %>', '<%= fontPath %>', '<%= imgPath %>',
          '<%= partialPath %>', '<%= templatePath %>']
    },
    concat: {
      options: {
        // Append a short comment of the path for each concatenated source file
        process: function(src, filepath) {
          return '// Source: ' + filepath + '\n' + src;
        }
      },
      app: {
        src: ['app/js/app.js', 'app/js/*/*.js'],
        dest: '<%= javascriptPath %><%= pkg.name %>.js'
      }
    },
    connect: {
      options: {
        protocol: 'http',
        hostname: 'localhost',
        port: 8080,
        base: '<%= appPath %>',
        directory: '<%= appPath %>'
      },
      devserver: {
        options: {
          livereload: true,
          open: 'http://localhost:8080/index.html'
        }
      },
      testserver: {}
    },
    copy: {
      dependencies: {
        files: [{
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular/',
          src: ['angular.js'],
          dest: '<%= javascriptPath %>'
        }, {
			expand: true,
			cwd: '<%= bowerrc.directory %>/angular-bootstrap/',
			src: ['ui-bootstrap-tpls.js'],
			dest: '<%= javascriptPath %>'
		}, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-route/',
          src: ['angular-route.js'],
          dest: '<%= javascriptPath %>'
		}, {
		  expand: true,
		  cwd: '<%= bowerrc.directory %>/angular-ui-router/release/',
		  src: ['angular-ui-router.js'],
		  dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/js/',
          src: ['bootstrap.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/restangular/dist',
          src: ['restangular.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/lodash',
          src: ['lodash.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/css/',
          src: ['bootstrap.css', 'bootstrap-theme.css'],
          dest: '<%= cssPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/fonts/',
          src: ['**'],
          dest: '<%= fontPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/jquery/dist/',
          src: ['jquery.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/jquery-mousewheel/',
          src: ['jquery.mousewheel.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-resource/',
          src: ['angular-resource.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-mocks/',
          src: ['angular-mocks.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/d3/',
          src: ['d3.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/jquery-ui/',
          src: ['jquery-ui.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/jquery-ui/themes/smoothness/',
          src: ['jquery-ui.css'],
          dest: '<%= cssPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/d3-cloud/',
          src: ['d3.layout.cloud.js'],
          dest: '<%= javascriptPath %>'
        }, {
          src: '<%= bowerrc.directory %>/d3-tip/index.js',
          dest: '<%= javascriptPath %>/d3.tooltip.js'
        }]
      },
      statics: {
        files: [{
          expand: true,
          cwd: 'app/',
          src: ['*.html'],
          dest: '<%= appPath %>'
        }, {
          expand: true,
          cwd: 'app/partials',
          src: ['**'],
          dest: '<%= partialPath %>'
        }, {
          expand: true,
          cwd: 'app/img',
          src: ['**'],
          dest: '<%= imgPath %>'
        }, {
          expand: true,
          cwd: 'app/templates',
          src: ['**'],
          dest: '<%= templatePath %>'
        }, {
          expand: true,
          cwd: 'app/js/',
          src: ['shared-worker.js'],
          dest: '<%= javascriptPath %>'
        }]
      },
      deploy: {
        files:[{
          expand: true,
          src: ['**'],
          cwd: '<%= appPath %>',
          dest: '<%= targetPath %>'
        }]
      }
    },
    karma: {
      dev: {
        configFile: '<%= mvnSrcDirectory %>test/front-end/karma.conf.js'
      },
      continuous: {
        configFile: '<%= mvnSrcDirectory %>test/front-end/karma.conf.ci.js'
      }
    },
    less: {
      development: {
        options: {
          strictMath: true
        },
        files: {
          '<%= cssPath %>style.css': 'app/less/style.less'
        }
      }
    },
    protractor: {
      options: {
        configFile: '<%= mvnSrcDirectory %>test/front-end/protractor.conf.js'
      },
      firefox: {
        options: {
          args: {
            browser: 'firefox'
          }
        }
      },
      chrome: {
        options: {
          args: {
            browser: 'chrome'
          }
        }
      }
    },
    watch: {
      options: {
        livereload: true
      },
      less: {
        files: ['app/less/**/*.less'],
        tasks: ['less', 'copy:deploy']
      },
      statics: {
        files: ['app/**/*.html', 'app/img/**'],
        tasks: ['copy:statics', 'copy:deploy']
      },
      scripts: {
        files: ['app/js/**/*.js'],
        tasks: ['concat', 'copy:statics', 'copy:deploy']
      },
      dependencies: {
        files: ['<%= bowerrc.directory %>/**'],
        tasks: ['copy:dependencies', 'copy:deploy']
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-karma');
  grunt.loadNpmTasks('grunt-protractor-runner');

  grunt.registerTask('build', ['clean', 'copy', 'concat', 'less']);
  grunt.registerTask('default', ['build']);
  grunt.registerTask('test', ['test:unit']);
  grunt.registerTask('test:gui', ['build', 'connect:testserver', 'protractor']);
  grunt.registerTask('test:gui:chrome', ['build', 'connect:testserver', 'protractor:chrome']);
  grunt.registerTask('test:gui:firefox', ['build', 'connect:testserver', 'protractor:firefox']);
  grunt.registerTask('test:unit', ['karma:dev']);
  grunt.registerTask('webserver', ['build', 'connect:devserver', 'watch']);
};
