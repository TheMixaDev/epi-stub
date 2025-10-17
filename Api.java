@PostMapping("/compile")
public ResponseBody<Void> compileCode(@RequestBody Script code) {
    CompileService.compile(code);
    return ok();
}
