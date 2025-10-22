@PostMapping("/compile")
public ResponseBody<Void> compileCode(@RequestBody Script code) {
    CompileService.compile(code);
    return ok();
}

@GetMapping("/metainf")
public ResponseBody<Info> getMetaInf(@RequestParam String uuid) {
    return new ResponseBody(MetaInfService.getInfo(uuid));
}
